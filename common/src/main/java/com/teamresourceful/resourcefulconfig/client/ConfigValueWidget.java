package com.teamresourceful.resourcefulconfig.client;

import com.google.common.collect.Lists;
import com.teamresourceful.resourcefulconfig.client.options.Options;
import com.teamresourceful.resourcefulconfig.common.annotations.Comment;
import com.teamresourceful.resourcefulconfig.common.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.common.config.ParsingUtils;
import com.teamresourceful.resourcefulconfig.common.config.ResourcefulConfigEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigValueWidget extends ValueWidget {

    private final int left;

    private final ResourcefulConfigEntry entry;

    private final Component label;
    private AbstractWidget children;
    private final AbstractWidget reset;

    private final Component tooltip;

    public ConfigValueWidget(int left, int right, ResourcefulConfigEntry entry) {
        this.left = left;
        this.entry = entry;
        var annotation = entry.field().getAnnotation(ConfigEntry.class);
        this.label = annotation == null ? CommonComponents.EMPTY : Component.translatable(annotation.translation());
        this.children = Options.create(right - 110, 0, 100, entry);
        this.reset = Button.builder(Component.literal("❌").withStyle(ChatFormatting.BOLD), (button) -> {
                    entry.reset();
                    this.children = Options.create(right - 110, 0, 100, entry);
                })
                .bounds(right - 140, 0, 20, 20)
                .build();
        this.reset.active = this.children.active && entry.isModified(ParsingUtils.getField(entry.field()));

        var comment = entry.field().getAnnotation(Comment.class);
        this.tooltip = comment == null ? CommonComponents.EMPTY : comment.translation().isEmpty() ? Component.literal(comment.value()) : Component.translatable(comment.translation());
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {

        Font font = Minecraft.getInstance().font;
        graphics.drawString(font, this.label, left + 10, j + 5, 16777215, false);

        this.reset.active = this.children.active && entry.isModified(ParsingUtils.getField(entry.field()));

        this.reset.setY(j);
        this.reset.render(graphics, n, o, f);

        this.children.setY(j);
        this.children.render(graphics, n, o, f);
    }

    public @NotNull List<? extends GuiEventListener> children() {
        return Lists.newArrayList(this.children, this.reset);
    }

    public @NotNull List<? extends NarratableEntry> narratables() {
        return Lists.newArrayList(this.children, this.reset);
    }

    public AbstractWidget getChildren() {
        return children;
    }

    public AbstractWidget getReset() {
        return reset;
    }

    @Override
    public List<FormattedCharSequence> getTooltip() {
        return Minecraft.getInstance().font.split(this.tooltip, 200);
    }
}
