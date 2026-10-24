package com.laodafeijibei.zhongyanfumotai.client.screen;

import com.laodafeijibei.zhongyanfumotai.enchant.ExperienceCost;
import com.laodafeijibei.zhongyanfumotai.enchant.EnchantmentsAccess;
import com.laodafeijibei.zhongyanfumotai.enchant.HighLevelEnchantments;
import com.laodafeijibei.zhongyanfumotai.client.search.PinyinSearch;
import com.laodafeijibei.zhongyanfumotai.menu.EndEnchantingMenu;
import com.laodafeijibei.zhongyanfumotai.network.ApplyEnchantmentsPacket;
import com.laodafeijibei.zhongyanfumotai.network.ModNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class EndEnchantingScreen extends AbstractContainerScreen<EndEnchantingMenu> {
    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 222;
    private static final int LIST_X = 8;
    private static final int LIST_Y = 43;
    private static final int LIST_WIDTH = 132;
    private static final int ROW_HEIGHT = 12;
    private static final int VISIBLE_ROWS = 13;

    private static final int OBSIDIAN = 0xFF120D1C;
    private static final int DEEP_PURPLE = 0xFF2E1645;
    private static final int PURPLE = 0xFF6B2E8B;
    private static final int BRIGHT_PURPLE = 0xFFB15CFF;
    private static final int PARCHMENT = 0xFFE8DDB7;
    private static final int PARCHMENT_DARK = 0xFF8E7D55;
    private static final int INK = 0xFF2A1C2E;

    private final List<EnchantmentEntry> allEnchantments = new ArrayList<>();
    private final List<DisplayEntry> filteredEntries = new ArrayList<>();
    private final Map<ResourceLocation, Integer> selectedLevels = new LinkedHashMap<>();

    private EditBox searchBox;
    private EditBox levelBox;
    private Button applyButton;
    private ResourceLocation editingId;
    private int scrollOffset;
    private boolean levelInputValid = true;
    private ItemStack lastTargetSnapshot = ItemStack.EMPTY;
    private final Registry<Enchantment> enchantmentRegistry;

    public EndEnchantingScreen(EndEnchantingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = PANEL_WIDTH;
        this.imageHeight = PANEL_HEIGHT;
        this.inventoryLabelX = 151;
        this.inventoryLabelY = 130;

        this.enchantmentRegistry = inventory.player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        enchantmentRegistry.entrySet().stream()
                .map(entry -> new EnchantmentEntry(entry.getKey().location(),
                        EnchantmentsAccess.resolve(enchantmentRegistry, entry.getKey().location())))
                .sorted(Comparator.comparing(entry -> entry.id().toString()))
                .forEach(allEnchantments::add);
        rebuildFilter("");
        lastTargetSnapshot = menu.getTargetStack().copy();
    }

    @Override
    protected void init() {
        super.init();
        searchBox = new EditBox(font, leftPos + 8, topPos + 18, 132, 18,
                Component.translatable("gui.end_enchanting_table.search"));
        searchBox.setHint(Component.translatable("gui.end_enchanting_table.search_hint")
                .withStyle(ChatFormatting.DARK_GRAY));
        searchBox.setMaxLength(128);
        searchBox.setResponder(this::rebuildFilter);
        addRenderableWidget(searchBox);

        levelBox = new EditBox(font, leftPos + 195, topPos + 50, 117, 18,
                Component.translatable("gui.end_enchanting_table.level"));
        levelBox.setHint(Component.translatable("gui.end_enchanting_table.level_hint")
                .withStyle(ChatFormatting.DARK_GRAY));
        levelBox.setMaxLength(10);
        levelBox.setFilter(EndEnchantingScreen::isPotentialLevel);
        levelBox.setResponder(this::updateEditingLevel);
        addRenderableWidget(levelBox);

        applyButton = addRenderableWidget(Button.builder(
                        Component.translatable("gui.end_enchanting_table.apply"), button -> sendApplyRequest())
                .bounds(leftPos + 195, topPos + 78, 117, 20)
                .build());
        updateApplyButton();
    }

    private static boolean isPotentialLevel(String text) {
        if (text.isEmpty()) {
            return true;
        }
        if (!text.chars().allMatch(Character::isDigit)) {
            return false;
        }
        try {
            long value = Long.parseLong(text);
            return value <= Integer.MAX_VALUE;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private void rebuildFilter(String query) {
        String trimmed = query == null ? "" : query.trim();
        Map<Holder<Enchantment>, Integer> current = currentEnchantments();
        List<EnchantmentEntry> matching = allEnchantments.stream()
                .filter(entry -> PinyinSearch.matches(trimmed, entry.id().toString(), entry.name().getString()))
                .sorted(Comparator.comparing((EnchantmentEntry entry) -> entry.name().getString(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(entry -> entry.id().toString()))
                .toList();
        filteredEntries.clear();
        addSection(filteredEntries, matching, current, true);
        addSection(filteredEntries, matching, current, false);
        scrollOffset = Math.min(scrollOffset, maxScroll());
    }

    private static void addSection(List<DisplayEntry> destination, List<EnchantmentEntry> matching,
                                   Map<Holder<Enchantment>, Integer> current, boolean enchanted) {
        List<EnchantmentEntry> section = matching.stream()
                .filter(entry -> (current.getOrDefault(entry.enchantment(), 0) > 0) == enchanted)
                .toList();
        if (!section.isEmpty()) {
            destination.add(DisplayEntry.header(enchanted
                    ? "gui.end_enchanting_table.enchanted"
                    : "gui.end_enchanting_table.not_enchanted"));
            section.forEach(entry -> destination.add(DisplayEntry.item(entry)));
        }
    }

    private void updateEditingLevel(String value) {
        if (editingId == null) {
            levelInputValid = value.isEmpty();
            updateApplyButton();
            return;
        }
        if (value.isEmpty()) {
            levelInputValid = false;
        } else {
            try {
                int level = Integer.parseInt(value);
                levelInputValid = level >= 0;
                if (levelInputValid) {
                    selectedLevels.put(editingId, level);
                }
            } catch (NumberFormatException ignored) {
                levelInputValid = false;
            }
        }
        updateApplyButton();
    }

    private void selectForEditing(EnchantmentEntry entry) {
        editingId = entry.id();
        int level = selectedLevels.computeIfAbsent(entry.id(), ignored -> {
            int currentLevel = currentEnchantments().getOrDefault(entry.enchantment(), 0);
            return currentLevel > 0 ? currentLevel : 1;
        });
        levelInputValid = true;
        levelBox.setValue(Integer.toString(level));
        levelBox.setFocused(true);
        setFocused(levelBox);
        levelBox.moveCursorToEnd(false);
        updateApplyButton();
    }

    private void toggleSelection(EnchantmentEntry entry) {
        if (selectedLevels.containsKey(entry.id())) {
            selectedLevels.remove(entry.id());
            if (entry.id().equals(editingId)) {
                editingId = null;
                levelBox.setValue("");
                levelBox.setFocused(false);
            }
        } else {
            selectForEditing(entry);
        }
        updateApplyButton();
    }

    private void sendApplyRequest() {
        if (!canApply()) {
            return;
        }
        ModNetwork.sendToServer(new ApplyEnchantmentsPacket(menu.containerId,
                new LinkedHashMap<>(selectedLevels)));
    }

    private boolean canApply() {
        if (minecraft == null || minecraft.player == null || selectedLevels.isEmpty()
                || !levelInputValid || menu.getTargetStack().isEmpty() || !hasEffectiveChanges()) {
            return false;
        }
        long settlement = calculateSettlement();
        return minecraft.player.getAbilities().instabuild || settlement <= minecraft.player.experienceLevel;
    }

    private void updateApplyButton() {
        if (applyButton != null) {
            applyButton.active = canApply();
        }
    }

    private long calculateSettlement() {
        Map<Holder<Enchantment>, Integer> targetLevels = new LinkedHashMap<>();
        selectedLevels.forEach((id, level) -> {
            Holder<Enchantment> enchantment = EnchantmentsAccess.resolve(enchantmentRegistry, id);
            if (enchantment != null) {
                targetLevels.put(enchantment, level);
            }
        });
        return ExperienceCost.calculate(currentEnchantments(), targetLevels);
    }

    private boolean hasEffectiveChanges() {
        Map<Holder<Enchantment>, Integer> currentLevels = currentEnchantments();
        return selectedLevels.entrySet().stream().anyMatch(entry -> {
            Holder<Enchantment> enchantment = EnchantmentsAccess.resolve(enchantmentRegistry, entry.getKey());
            return enchantment != null
                    && Math.max(0, currentLevels.getOrDefault(enchantment, 0)) != entry.getValue();
        });
    }

    private Map<Holder<Enchantment>, Integer> currentEnchantments() {
        ItemStack target = menu.getTargetStack();
        return target.isEmpty() ? Map.of() : HighLevelEnchantments.current(target);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        ItemStack currentTarget = menu.getTargetStack();
        if (!sameStack(lastTargetSnapshot, currentTarget)) {
            selectedLevels.clear();
            editingId = null;
            levelInputValid = true;
            if (levelBox != null) {
                levelBox.setValue("");
                levelBox.setFocused(false);
            }
            setFocused(null);
            lastTargetSnapshot = currentTarget.copy();
            rebuildFilter(searchBox == null ? "" : searchBox.getValue());
        }
        updateApplyButton();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        graphics.fill(x, y, x + imageWidth, y + imageHeight, OBSIDIAN);
        graphics.renderOutline(x, y, imageWidth, imageHeight, PARCHMENT_DARK);

        graphics.fill(x + 4, y + 4, x + 144, y + imageHeight - 4, DEEP_PURPLE);
        graphics.renderOutline(x + 4, y + 4, 140, imageHeight - 8, PURPLE);
        graphics.fill(x + 146, y + 4, x + imageWidth - 4, y + 126, PARCHMENT);
        graphics.renderOutline(x + 146, y + 4, imageWidth - 150, 122, PARCHMENT_DARK);
        graphics.fill(x + 146, y + 126, x + imageWidth - 4, y + imageHeight - 4, 0xFF21172A);
        graphics.renderOutline(x + 146, y + 126, imageWidth - 150, imageHeight - 130, PURPLE);

        for (Slot slot : menu.slots) {
            int slotX = x + slot.x - 1;
            int slotY = y + slot.y - 1;
            int border = slot == menu.slots.get(EndEnchantingMenu.TARGET_SLOT) ? PARCHMENT_DARK : PURPLE;
            graphics.fill(slotX, slotY, slotX + 18, slotY + 18, border);
            graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0xFF09070D);
        }

        int first = scrollOffset;
        int last = Math.min(filteredEntries.size(), first + VISIBLE_ROWS);
        graphics.enableScissor(x + LIST_X, y + LIST_Y, x + LIST_X + LIST_WIDTH, y + LIST_Y + ROW_HEIGHT * VISIBLE_ROWS);
        for (int index = first; index < last; index++) {
            DisplayEntry displayEntry = filteredEntries.get(index);
            int rowY = y + LIST_Y + (index - first) * ROW_HEIGHT;
            if (displayEntry.isHeader()) {
                graphics.fill(x + LIST_X, rowY, x + LIST_X + LIST_WIDTH, rowY + ROW_HEIGHT, 0x663B2058);
                graphics.drawString(font, Component.translatable(displayEntry.headerKey()),
                        x + LIST_X + 3, rowY + 2, 0xFFFFD98A, false);
                continue;
            }
            EnchantmentEntry entry = displayEntry.enchantment();
            boolean hovered = mouseX >= x + LIST_X && mouseX < x + LIST_X + LIST_WIDTH
                    && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;
            boolean selected = selectedLevels.containsKey(entry.id());
            if (hovered) {
                graphics.fill(x + LIST_X, rowY, x + LIST_X + LIST_WIDTH, rowY + ROW_HEIGHT, 0x554A2370);
            }
            int boxColor = selected ? BRIGHT_PURPLE : PARCHMENT_DARK;
            graphics.renderOutline(x + LIST_X + 2, rowY + 2, 8, 8, boxColor);
            if (selected) {
                graphics.fill(x + LIST_X + 4, rowY + 4, x + LIST_X + 8, rowY + 8, BRIGHT_PURPLE);
            }
            FormattedCharSequence clipped = font.split(entry.name(), 103).get(0);
            int textColor = entry.enchantment().is(EnchantmentTags.CURSE) ? 0xFFFF7777 : 0xFFF0E8CF;
            graphics.drawString(font, clipped, x + LIST_X + 14, rowY + 2, textColor, false);
        }
        graphics.disableScissor();

        if (maxScroll() > 0) {
            int trackY = y + LIST_Y;
            int trackHeight = ROW_HEIGHT * VISIBLE_ROWS;
            int thumbHeight = Math.max(12, trackHeight * VISIBLE_ROWS / filteredEntries.size());
            int thumbY = trackY + (trackHeight - thumbHeight) * scrollOffset / maxScroll();
            graphics.fill(x + 137, trackY, x + 140, trackY + trackHeight, 0xFF0A0710);
            graphics.fill(x + 137, thumbY, x + 140, thumbY + thumbHeight, BRIGHT_PURPLE);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 151, 8, INK, false);
        graphics.drawString(font, Component.translatable("gui.end_enchanting_table.target"), 177, 35, INK, false);
        graphics.drawString(font, Component.translatable("gui.end_enchanting_table.level"), 195, 38, INK, false);

        Component editing = editingId == null
                ? Component.translatable("gui.end_enchanting_table.none_selected")
                : Component.translatable("gui.end_enchanting_table.editing",
                Enchantment.getFullname(EnchantmentsAccess.resolve(enchantmentRegistry, editingId), selectedLevels.get(editingId)));
        graphics.drawString(font, font.split(editing, 158).get(0), 151, 104, INK, false);

        long settlement = calculateSettlement();
        int settlementColor = canAfford(settlement) ? 0xFF2B6334 : 0xFFA52D2D;
        Component settlementText = settlement > 0L
                ? Component.translatable("gui.end_enchanting_table.cost", formatAmount(settlement))
                : settlement < 0L
                ? Component.translatable("gui.end_enchanting_table.refund", formatAmount(-settlement))
                : Component.translatable("gui.end_enchanting_table.balanced");
        graphics.drawString(font, Component.translatable("gui.end_enchanting_table.selected", selectedLevels.size()),
                151, 116, INK, false);
        graphics.drawString(font, settlementText, 229, 116, settlementColor, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFE8DDB7, false);
    }

    private boolean canAfford(long settlement) {
        return minecraft != null && minecraft.player != null
                && (minecraft.player.getAbilities().instabuild || settlement <= minecraft.player.experienceLevel);
    }

    private static String formatAmount(long value) {
        String digits = Long.toString(value);
        StringBuilder formatted = new StringBuilder(digits.length() + digits.length() / 3);
        for (int i = 0; i < digits.length(); i++) {
            if (i > 0 && (digits.length() - i) % 3 == 0) {
                formatted.append(',');
            }
            formatted.append(digits.charAt(i));
        }
        return formatted.toString();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        EnchantmentEntry hovered = hoveredEntry(mouseX, mouseY);
        if (hovered != null) {
            String categoryKey = hovered.enchantment().is(EnchantmentTags.CURSE)
                    ? "gui.end_enchanting_table.category.curse"
                    : hovered.enchantment().is(EnchantmentTags.TREASURE)
                    ? "gui.end_enchanting_table.category.treasure"
                    : "gui.end_enchanting_table.category.normal";
            int multiplier = ExperienceCost.multiplier(hovered.enchantment());
            List<FormattedText> tooltip = new ArrayList<>();
            tooltip.add(hovered.name());
            String descriptionKey = "enchantment." + hovered.id().getNamespace() + "." + hovered.id().getPath() + ".desc";
            if (Language.getInstance().has(descriptionKey)) {
                tooltip.add(Component.translatable(descriptionKey).withStyle(ChatFormatting.WHITE));
            }
            tooltip.add(Component.translatable(categoryKey, hovered.id().toString(), multiplier).withStyle(ChatFormatting.GRAY));
            graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY, ItemStack.EMPTY);
        }
    }

    private EnchantmentEntry hoveredEntry(double mouseX, double mouseY) {
        double localX = mouseX - leftPos;
        double localY = mouseY - topPos;
        if (localX < LIST_X || localX >= LIST_X + LIST_WIDTH
                || localY < LIST_Y || localY >= LIST_Y + ROW_HEIGHT * VISIBLE_ROWS) {
            return null;
        }
        int index = scrollOffset + (int) ((localY - LIST_Y) / ROW_HEIGHT);
        if (index < 0 || index >= filteredEntries.size()) {
            return null;
        }
        DisplayEntry displayEntry = filteredEntries.get(index);
        return displayEntry.isHeader() ? null : displayEntry.enchantment();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        EnchantmentEntry entry = hoveredEntry(mouseX, mouseY);
        if (entry != null && button == 0) {
            double localX = mouseX - leftPos;
            if (localX < LIST_X + 13) {
                toggleSelection(entry);
            } else {
                selectForEditing(entry);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta, double deltaY) {
        if (mouseX >= leftPos + LIST_X && mouseX < leftPos + LIST_X + LIST_WIDTH
                && mouseY >= topPos + LIST_Y && mouseY < topPos + LIST_Y + ROW_HEIGHT * VISIBLE_ROWS) {
            scrollOffset = Math.max(0, Math.min(maxScroll(), scrollOffset - (int) Math.signum(delta)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta, deltaY);
    }

    private int maxScroll() {
        return Math.max(0, filteredEntries.size() - VISIBLE_ROWS);
    }

    private static boolean sameStack(ItemStack first, ItemStack second) {
        return ItemStack.isSameItemSameComponents(first, second);
    }

    private record EnchantmentEntry(ResourceLocation id, Holder<Enchantment> enchantment) {
        Component name() {
            return enchantment.value().description();
        }
    }

    private record DisplayEntry(String headerKey, EnchantmentEntry enchantment) {
        static DisplayEntry header(String key) {
            return new DisplayEntry(key, null);
        }

        static DisplayEntry item(EnchantmentEntry entry) {
            return new DisplayEntry(null, entry);
        }

        boolean isHeader() {
            return enchantment == null;
        }
    }
}
