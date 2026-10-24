[CmdletBinding()]
param(
    [string]$JarPath = '',
    [string]$InstanceModsDirectory = 'D:\mc\.minecraft\versions\Closing Song1.6.5\mods',
    [string]$PclCacheDirectory = (Join-Path $env:TEMP 'PCL\Cache'),
    [string]$PclAssetDirectory = 'D:\mc\PCL\LocalModAssets'
)

$ErrorActionPreference = 'Stop'
$chineseName = -join @([char]0x7EC8, [char]0x7109, [char]0x9644, [char]0x9B54, [char]0x53F0)
$englishName = 'End Enchanting Table'
$displayName = $chineseName + ' | ' + $englishName
$releaseFileName = $englishName + '.jar'
$legacyReleaseFileNames = @(
    $englishName + [char]0xFF08 + $chineseName + [char]0xFF09 + '.jar'
)
$gameVersion = '1.20.1'
$modVersion = '1.1.2'

if ([string]::IsNullOrWhiteSpace($JarPath)) {
    $JarPath = Join-Path (Split-Path -Parent $PSScriptRoot) (Join-Path 'dist' $releaseFileName)
}

function Resolve-ContainedPath {
    param(
        [Parameter(Mandatory = $true)][string]$Parent,
        [Parameter(Mandatory = $true)][string]$Child
    )

    $resolvedParent = [IO.Path]::GetFullPath($Parent).TrimEnd([IO.Path]::DirectorySeparatorChar)
    $resolvedChild = [IO.Path]::GetFullPath((Join-Path $resolvedParent $Child))
    if (-not $resolvedChild.StartsWith($resolvedParent + [IO.Path]::DirectorySeparatorChar, [StringComparison]::OrdinalIgnoreCase)) {
        throw "Path escaped its expected directory: $resolvedChild"
    }
    return $resolvedChild
}

$resolvedJar = [IO.Path]::GetFullPath($JarPath)
if (-not (Test-Path -LiteralPath $resolvedJar -PathType Leaf)) {
    throw "Release JAR not found: $resolvedJar"
}

$pclProcesses = @(Get-Process -ErrorAction SilentlyContinue | Where-Object {
    $_.ProcessName -like 'Plain Craft Launcher*'
})
if ($pclProcesses.Count -gt 0) {
    throw 'Plain Craft Launcher is running. Close PCL before installing its local display cache.'
}

$resolvedModsDirectory = [IO.Path]::GetFullPath($InstanceModsDirectory)
$resolvedCacheDirectory = [IO.Path]::GetFullPath($PclCacheDirectory)
$resolvedAssetDirectory = [IO.Path]::GetFullPath($PclAssetDirectory)
foreach ($directory in @($resolvedModsDirectory, $resolvedCacheDirectory, $resolvedAssetDirectory)) {
    [IO.Directory]::CreateDirectory($directory) | Out-Null
}

$installedJar = Resolve-ContainedPath -Parent $resolvedModsDirectory -Child $releaseFileName
if (-not [string]::Equals($resolvedJar, $installedJar, [StringComparison]::OrdinalIgnoreCase)) {
    Copy-Item -LiteralPath $resolvedJar -Destination $installedJar -Force
}
foreach ($legacyReleaseFileName in $legacyReleaseFileNames) {
    $legacyInstalledJar = Resolve-ContainedPath -Parent $resolvedModsDirectory -Child $legacyReleaseFileName
    if (Test-Path -LiteralPath $legacyInstalledJar -PathType Leaf) {
        Remove-Item -LiteralPath $legacyInstalledJar -Force
    }
}

$logoPath = Resolve-ContainedPath -Parent $resolvedAssetDirectory -Child 'end_enchanting_table.png'
Add-Type -AssemblyName System.IO.Compression.FileSystem
$archive = [IO.Compression.ZipFile]::OpenRead($resolvedJar)
try {
    $logoEntry = $archive.GetEntry('end_enchanting_table.png')
    if ($null -eq $logoEntry) {
        throw 'The release JAR does not contain end_enchanting_table.png.'
    }
    [IO.Compression.ZipFileExtensions]::ExtractToFile($logoEntry, $logoPath, $true)
}
finally {
    $archive.Dispose()
}

$sha1 = (Get-FileHash -Algorithm SHA1 -LiteralPath $resolvedJar).Hash.ToLowerInvariant()
$sha256 = (Get-FileHash -Algorithm SHA256 -LiteralPath $resolvedJar).Hash.ToUpperInvariant()
$cacheKey = $sha1 + $gameVersion + '1'
$cachePath = Resolve-ContainedPath -Parent $resolvedCacheDirectory -Child 'LocalMod.json'
if (-not (Test-Path -LiteralPath $cachePath -PathType Leaf)) {
    throw "PCL local Mod cache not found: $cachePath. Open PCL's Mod management page once, then rerun this script."
}

$cacheRaw = [IO.File]::ReadAllText($cachePath, [Text.Encoding]::UTF8)
$cache = $cacheRaw | ConvertFrom-Json
if ($null -eq $cache.version) {
    throw "Unrecognized PCL cache format: $cachePath"
}

$backupDirectory = Resolve-ContainedPath -Parent $resolvedCacheDirectory -Child 'EndEnchantingTableBackup'
[IO.Directory]::CreateDirectory($backupDirectory) | Out-Null
$backupPath = Resolve-ContainedPath -Parent $backupDirectory -Child 'LocalMod.before-end-enchanting-table.json'
if (-not (Test-Path -LiteralPath $backupPath)) {
    Copy-Item -LiteralPath $cachePath -Destination $backupPath
}

$record = [ordered]@{
    Project = [ordered]@{
        Platform = 2
        Types = 1
        Slug = 'end-enchanting-table'
        Id = 'end_enchanting_table'
        RawName = $displayName
        Description = 'An enchanting table with full 32-bit levels, batch enchanting, downgrading, and disenchanting.'
        Website = ''
        LastUpdate = '2026-07-29T20:00:00+08:00'
        DownloadCount = 0
        ModLoaders = 1
        Tags = @('Magic', 'Utility')
        LogoUrl = $logoPath
        Drops = @(200)
        CacheTime = '2099-12-31T23:59:59+08:00'
    }
    ChangelogUrls = @()
    OnlineDataLoaded = $true
    Version = [ordered]@{
        ResourceType = 1
        Id = 'end_enchanting_table-1.1.2-local'
        Version = $modVersion
        DisplayName = $displayName
        ReleaseDate = '2026-07-29T20:00:00+08:00'
        DownloadCount = 0
        ModLoaders = 1
        GameVersions = @($gameVersion)
        ReleaseType = 1
        FileName = $releaseFileName
        DownloadUrls = @()
        Hash = $sha1
        Size = (Get-Item -LiteralPath $resolvedJar).Length
        RawDependencies = @()
        Dependencies = @()
    }
}

# Remove records for older builds of this Mod. Their cache keys contain an old
# JAR hash and otherwise survive metadata-only rebuilds as stale duplicates.
@($cache.PSObject.Properties) | Where-Object {
    $_.Name -ne 'version' -and
    ($_.Value.Project.Id -eq 'end_enchanting_table' -or
     $_.Value.Project.Slug -eq 'end-enchanting-table')
} | ForEach-Object {
    $cache.PSObject.Properties.Remove($_.Name)
}
$cache | Add-Member -NotePropertyName $cacheKey -NotePropertyValue $record
$utf8WithoutBom = New-Object Text.UTF8Encoding($false)
[IO.File]::WriteAllText($cachePath, ($cache | ConvertTo-Json -Depth 100 -Compress), $utf8WithoutBom)

$verification = [IO.File]::ReadAllText($cachePath, [Text.Encoding]::UTF8) | ConvertFrom-Json
$installedRecord = $verification.PSObject.Properties[$cacheKey]
if ($null -eq $installedRecord -or $installedRecord.Value.Project.RawName -ne $displayName) {
    throw 'PCL display cache verification failed.'
}
if (-not (Test-Path -LiteralPath $logoPath -PathType Leaf)) {
    throw 'PCL cover extraction verification failed.'
}
if ((Get-FileHash -Algorithm SHA256 -LiteralPath $installedJar).Hash -ne $sha256) {
    throw 'Installed JAR hash does not match the release JAR.'
}

[PSCustomObject]@{
    DisplayName = $displayName
    InstalledJar = $installedJar
    Cover = $logoPath
    CacheKey = $cacheKey
    SHA256 = $sha256
    Backup = $backupPath
}
