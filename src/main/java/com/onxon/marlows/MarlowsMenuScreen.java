package com.onxon.marlows;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarlowsMenuScreen extends Screen {
    private String section = "home";
    private final List<TextFieldWidget> fields = new ArrayList<>();
    private final List<String> fieldKeys = new ArrayList<>();
    private final List<Map<String, String>> fieldMaps = new ArrayList<>();

    public MarlowsMenuScreen() { super(Text.literal("Onxon's rig mod")); }

    @Override
    protected void init() {
        clearChildren();
        fields.clear(); fieldKeys.clear(); fieldMaps.clear();
        int cx = width / 2;
        if (section.equals("home")) {
            addTitleButton("Paper", cx, 75, () -> open("paper"));
            addTitleButton("BJ", cx, 105, () -> open("bj"));
            addTitleButton("50/50", cx, 135, () -> open("50/50"));
            addTitleButton("Odd or Even", cx, 165, () -> open("oddoreven"));
            addTitleButton("RR", cx, 195, () -> open("rr"));
        } else {
            addDrawableChild(ButtonWidget.builder(Text.literal("< Back"), b -> open("home"))
                    .dimensions(20, 20, 75, 20).build());
            if (section.equals("paper")) {
                addLabel(50, 62, "Assign an item ID to each number (example: minecraft:diamond)");
                addMapFields(CONFIG.paperItems, 1, 9, 92);
                addLabel(50, 220, "Paper controls: Paper Left / Paper Right / Paper Reset");
                addLabel(50, 236, "Default menu key: O. Rebind everything in Controls.");
            } else if (section.equals("bj")) {
                addLabel(50, 62, "Assign the map/item ID used for each BJ value");
                int y = 90;
                for (String key : MarlowsClient.bjNumbers()) {
                    addSingleField(CONFIG.bjMaps, key, 50, y);
                    y += 25;
                }
                addLabel(50, 330, "Each BJ value has its own keybind. Use BJ Reset to clear the visual override.");
            } else if (section.equals("50/50")) {
                addLabel(50, 70, "Left stack item ID");
                addSingleField("fiftyLeft", CONFIG.fiftyLeft, 50, 92);
                addLabel(50, 130, "Right stack item ID");
                addSingleField("fiftyRight", CONFIG.fiftyRight, 50, 152);
                addLabel(50, 200, "Controls: 50/50 Left Wins, Right Wins, Reset");
            } else if (section.equals("oddoreven")) {
                addLabel(50, 58, "Assign four item IDs for Odd and four for Even");
                addLabel(50, 78, "Odd");
                addMapFields(CONFIG.oddItems, 1, 4, 98);
                addLabel(50, 215, "Even");
                addMapFields(CONFIG.evenItems, 1, 4, 235);
                addLabel(50, 350, "Or item ID");
                addSingleField("orItem", CONFIG.orItem, 50, 370);
                addLabel(50, 405, "Controls: Odd, Even, Or, Reset");
            } else {
                addLabel(50, 95, "RR section");
                addLabel(50, 120, "Reserved for a future roulette configuration.");
            }
            addDrawableChild(ButtonWidget.builder(Text.literal("Save settings"), b -> saveFields())
                    .dimensions(width - 145, height - 38, 125, 20).build());
        }
    }

    private void addTitleButton(String text, int cx, int y, Runnable action) {
        addDrawableChild(ButtonWidget.builder(Text.literal(text), b -> action.run())
                .dimensions(cx - 105, y, 210, 23).build());
    }

    private void addLabel(int x, int y, String text) {
        // Labels are drawn in render to keep the screen lightweight.
        labels.add(new Label(x, y, text));
    }

    private final List<Label> labels = new ArrayList<>();

    private void addMapFields(Map<String, String> map, int start, int end, int y) {
        for (int i = start; i <= end; i++) addSingleField(map, String.valueOf(i), 50, y + (i-start)*25);
    }

    private void addSingleField(Map<String, String> map, String key, int x, int y) {
        TextFieldWidget field = new TextFieldWidget(textRenderer, x, y, 270, 20, Text.literal(key));
        field.setMaxLength(100);
        field.setText(map.getOrDefault(key, "minecraft:stone"));
        addDrawableChild(field);
        fields.add(field); fieldKeys.add(key); fieldMaps.add(map);
        addLabel(x + 280, y + 5, key + " =");
    }

    private void addSingleField(String key, String value, int x, int y) {
        TextFieldWidget field = new TextFieldWidget(textRenderer, x, y, 270, 20, Text.literal(key));
        field.setMaxLength(100); field.setText(value);
        addDrawableChild(field); fields.add(field); fieldKeys.add(key); fieldMaps.add(null);
    }

    private void saveFields() {
        for (int i = 0; i < fields.size(); i++) {
            String key = fieldKeys.get(i), value = fields.get(i).getText().trim();
            if (fieldMaps.get(i) != null) fieldMaps.get(i).put(key, value);
            else if (key.equals("fiftyLeft")) CONFIG.fiftyLeft = value;
            else if (key.equals("fiftyRight")) CONFIG.fiftyRight = value;
            else if (key.equals("orItem")) CONFIG.orItem = value;
        }
        CONFIG.save();
    }

    private void open(String next) { section = next; labels.clear(); clearAndInit(); }
    private void clearAndInit() { clearChildren(); init(); }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, width, 42, 0xCC101522);
        context.drawCenteredTextWithShadow(textRenderer, "ONXON'S RIG MOD", width / 2, 14, 0xFFB56CFF);
        context.drawCenteredTextWithShadow(textRenderer, section.equals("home") ? "Marlow's crystal optimiser" : section.toUpperCase(), width / 2, 50, 0xFFFFFFFF);
        for (Label label : labels) context.drawTextWithShadow(textRenderer, label.text, label.x, label.y, 0xFFD5D9E5);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() { return false; }

    private record Label(int x, int y, String text) {}
}
