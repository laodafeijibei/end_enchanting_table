param(
    [string]$SourceImage = 'C:\Users\yasuo\Desktop\3pXbHZJlDb\zhongyanfumotai.png'
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing

$projectRoot = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$resourceRoot = Join-Path $projectRoot 'src\main\resources'
$textureRoot = Join-Path $resourceRoot 'assets\end_enchanting_table\textures'
$itemTextureDir = Join-Path $textureRoot 'item'
$blockTextureDir = Join-Path $textureRoot 'block'
$entityTextureDir = Join-Path $textureRoot 'entity'
$vanillaBook = Join-Path $projectRoot '.cache\vanilla-assets\assets\minecraft\textures\entity\enchanting_table_book.png'

if (-not (Test-Path -LiteralPath $SourceImage)) {
    throw "Missing source image: $SourceImage"
}
if (-not (Test-Path -LiteralPath $vanillaBook)) {
    $clientJar = Join-Path $env:USERPROFILE '.gradle\caches\forge_gradle\minecraft_repo\versions\1.20.1\client.jar'
    if (-not (Test-Path -LiteralPath $clientJar)) {
        throw "Missing ForgeGradle Minecraft 1.20.1 client.jar; run gradlew compileJava first"
    }
    $extractRoot = Join-Path $projectRoot '.cache\vanilla-assets'
    New-Item -ItemType Directory -Force -Path $extractRoot | Out-Null
    Push-Location $extractRoot
    try {
        jar xf $clientJar 'assets/minecraft/textures/entity/enchanting_table_book.png'
    } finally {
        Pop-Location
    }
}

New-Item -ItemType Directory -Force -Path $itemTextureDir, $blockTextureDir, $entityTextureDir | Out-Null
Copy-Item -LiteralPath $SourceImage -Destination (Join-Path $resourceRoot 'end_enchanting_table.png') -Force

if (-not ('BorderConnectedBackground' -as [type])) {
    Add-Type -ReferencedAssemblies System.Drawing -TypeDefinition @'
using System;
using System.Drawing;
using System.Drawing.Imaging;

public static class BorderConnectedBackground
{
    public static void Remove(string inputPath, string outputPath, int threshold)
    {
        using (var source = new Bitmap(inputPath))
        using (var output = new Bitmap(source.Width, source.Height, PixelFormat.Format32bppArgb))
        {
            using (var graphics = Graphics.FromImage(output))
            {
                graphics.DrawImageUnscaled(source, 0, 0);
            }

            for (int y = 0; y < output.Height; y++)
            {
                for (int x = 0; x < output.Width; x++)
                {
                    Color color = output.GetPixel(x, y);
                    if (Math.Max(color.R, Math.Max(color.G, color.B)) > threshold) break;
                    output.SetPixel(x, y, Color.Transparent);
                }
                for (int x = output.Width - 1; x >= 0; x--)
                {
                    Color color = output.GetPixel(x, y);
                    if (Math.Max(color.R, Math.Max(color.G, color.B)) > threshold) break;
                    output.SetPixel(x, y, Color.Transparent);
                }
            }

            output.Save(outputPath, ImageFormat.Png);
        }
    }
}
'@
}

$itemTexture = Join-Path $itemTextureDir 'end_enchanting_table.png'
[BorderConnectedBackground]::Remove($SourceImage, $itemTexture, 6)

function New-PixelTexture {
    param(
        [string]$Path,
        [scriptblock]$Painter
    )
    $bitmap = New-Object System.Drawing.Bitmap 16, 16, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    try {
        & $Painter $bitmap
        $bitmap.Save($Path, [System.Drawing.Imaging.ImageFormat]::Png)
    } finally {
        $bitmap.Dispose()
    }
}

$obsidian = [System.Drawing.Color]::FromArgb(255, 12, 8, 20)
$obsidianLight = [System.Drawing.Color]::FromArgb(255, 29, 17, 45)
$purpleDark = [System.Drawing.Color]::FromArgb(255, 47, 18, 68)
$purple = [System.Drawing.Color]::FromArgb(255, 103, 39, 132)
$purpleBright = [System.Drawing.Color]::FromArgb(255, 181, 72, 255)
$ivoryDark = [System.Drawing.Color]::FromArgb(255, 145, 135, 102)
$ivory = [System.Drawing.Color]::FromArgb(255, 225, 213, 170)
$ivoryLight = [System.Drawing.Color]::FromArgb(255, 250, 239, 199)

New-PixelTexture (Join-Path $blockTextureDir 'end_enchanting_table_side.png') {
    param($bitmap)
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $base = if ((($x + $y) % 5) -eq 0) { $obsidianLight } else { $obsidian }
            $bitmap.SetPixel($x, $y, $base)
        }
    }
    for ($x = 0; $x -lt 16; $x++) {
        $bitmap.SetPixel($x, 4, $purple)
        $bitmap.SetPixel($x, 5, $purpleDark)
        $bitmap.SetPixel($x, 8, $purpleDark)
    }
    for ($y = 4; $y -le 7; $y++) {
        for ($x = 0; $x -le 3; $x++) {
            $bitmap.SetPixel($x, $y, $(if (($x + $y) % 2) { $ivory } else { $ivoryLight }))
            $bitmap.SetPixel(15 - $x, $y, $(if (($x + $y) % 2) { $ivoryDark } else { $ivory }))
        }
    }
    $runes = @(@(4,10), @(6,12), @(9,9), @(11,13), @(13,11))
    foreach ($rune in $runes) {
        $bitmap.SetPixel($rune[0], $rune[1], $purpleBright)
        if ($rune[0] + 1 -lt 16) { $bitmap.SetPixel($rune[0] + 1, $rune[1], $purple) }
    }
}

New-PixelTexture (Join-Path $blockTextureDir 'end_enchanting_table_top.png') {
    param($bitmap)
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $color = if ((($x * 3 + $y) % 7) -eq 0) { $purple } else { $purpleDark }
            $bitmap.SetPixel($x, $y, $color)
        }
    }
    foreach ($corner in @(@(0,0), @(12,0), @(0,12), @(12,12))) {
        for ($dy = 0; $dy -lt 4; $dy++) {
            for ($dx = 0; $dx -lt 4; $dx++) {
                $bitmap.SetPixel($corner[0] + $dx, $corner[1] + $dy,
                    $(if ((($dx + $dy) % 3) -eq 0) { $ivoryLight } else { $ivory }))
            }
        }
    }
    for ($y = 4; $y -lt 12; $y++) {
        for ($x = 4; $x -lt 12; $x++) {
            $bitmap.SetPixel($x, $y, $(if ((($x + $y) % 4) -eq 0) { $obsidianLight } else { $obsidian }))
        }
    }
    foreach ($point in @(@(7,5), @(9,7), @(6,9), @(10,10))) {
        $bitmap.SetPixel($point[0], $point[1], $purpleBright)
    }
}

New-PixelTexture (Join-Path $blockTextureDir 'end_enchanting_table_bottom.png') {
    param($bitmap)
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $choice = ($x * 7 + $y * 11) % 9
            $color = if ($choice -eq 0) { $purpleDark } elseif ($choice -le 2) { $obsidianLight } else { $obsidian }
            $bitmap.SetPixel($x, $y, $color)
        }
    }
}

$bookOutput = Join-Path $entityTextureDir 'end_enchanting_table_book.png'
$book = [System.Drawing.Bitmap]::FromFile($vanillaBook)
try {
    $recolors = @{
        '774E22' = [System.Drawing.Color]::FromArgb(255, 38, 14, 57)
        '875622' = [System.Drawing.Color]::FromArgb(255, 52, 18, 75)
        'A76E2D' = [System.Drawing.Color]::FromArgb(255, 91, 31, 119)
        'C58439' = [System.Drawing.Color]::FromArgb(255, 133, 53, 171)
        'FFD800' = [System.Drawing.Color]::FromArgb(255, 190, 91, 255)
        'FFB100' = [System.Drawing.Color]::FromArgb(255, 150, 53, 213)
    }
    for ($y = 0; $y -lt $book.Height; $y++) {
        for ($x = 0; $x -lt $book.Width; $x++) {
            $color = $book.GetPixel($x, $y)
            $key = ('{0:X2}{1:X2}{2:X2}' -f $color.R, $color.G, $color.B)
            if ($recolors.ContainsKey($key)) {
                $book.SetPixel($x, $y, $recolors[$key])
            }
        }
    }
    $book.Save($bookOutput, [System.Drawing.Imaging.ImageFormat]::Png)
} finally {
    $book.Dispose()
}

$icon = [System.Drawing.Bitmap]::FromFile($itemTexture)
try {
    $cornerAlpha = @(
        $icon.GetPixel(0, 0).A,
        $icon.GetPixel($icon.Width - 1, 0).A,
        $icon.GetPixel(0, $icon.Height - 1).A,
        $icon.GetPixel($icon.Width - 1, $icon.Height - 1).A
    )
    $transparentSamples = 0
    $opaqueSamples = 0
    for ($y = 0; $y -lt $icon.Height; $y += 8) {
        for ($x = 0; $x -lt $icon.Width; $x += 8) {
            if ($icon.GetPixel($x, $y).A -eq 0) { $transparentSamples++ } else { $opaqueSamples++ }
        }
    }
    [PSCustomObject]@{
        ItemTexture = $itemTexture
        Width = $icon.Width
        Height = $icon.Height
        CornerAlpha = ($cornerAlpha -join ',')
        TransparentSamples = $transparentSamples
        SubjectSamples = $opaqueSamples
    }
} finally {
    $icon.Dispose()
}

Get-FileHash -Algorithm SHA256 -LiteralPath $itemTexture, $bookOutput,
    (Join-Path $blockTextureDir 'end_enchanting_table_side.png'),
    (Join-Path $blockTextureDir 'end_enchanting_table_top.png'),
    (Join-Path $blockTextureDir 'end_enchanting_table_bottom.png')
