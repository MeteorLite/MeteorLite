package meteor.ui.client;

import com.google.common.base.Splitter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTooltip;
import com.sun.javafx.collections.ObservableListWrapper;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import meteor.MeteorLiteClientLauncher;
import meteor.MeteorLiteClientModule;
import meteor.config.Button;
import meteor.config.Config;
import meteor.config.ConfigDescriptor;
import meteor.config.ConfigItemDescriptor;
import meteor.config.ConfigManager;
import meteor.config.ConfigSection;
import meteor.config.ConfigSectionDescriptor;
import meteor.config.Keybind;
import meteor.config.ModifierlessKeybind;
import meteor.config.Range;
import meteor.eventbus.EventBus;
import meteor.plugins.Plugin;
import meteor.ui.MeteorUI;
import meteor.ui.components.ConfigButton;
import meteor.ui.components.ConfigToggleButton;
import meteor.ui.components.MeteorText;
import meteor.ui.components.SectionPane;
import meteor.util.MeteorConstants;
import net.runelite.api.Client;
import net.runelite.api.events.ConfigButtonClicked;
import org.sponge.util.Logger;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.awt.event.KeyEvent.getExtendedKeyCodeForChar;

public class PluginConfigPanel extends AnchorPane {

	private static final Logger logger = new Logger("PluginConfigController");
	private final Map<String, SectionPane> sections = new HashMap<>();
	private final Plugin plugin;
	private final AnchorPane titlePanel;
	private final VBox configList;
	private final Text pluginTitle;
	private final JFXButton backButton = new JFXButton("Back");
	private final Map<String, Boolean> openedCache = new HashMap<>();
	@Inject
	private Client client;
	@Inject
	private ConfigManager configManager;
	@Inject
	private MeteorUI meteorUI;
	@Inject
	private EventBus eventBus;
	private PluginToggleButton toggleButton;

	public PluginConfigPanel(Plugin plugin) {
		MeteorLiteClientLauncher.injector.injectMembers(this);

		this.plugin = plugin;
		titlePanel = new AnchorPane();
		AnchorPane.setTopAnchor(titlePanel, 0.0);
		AnchorPane.setLeftAnchor(titlePanel, 0.0);
		AnchorPane.setRightAnchor(titlePanel, 0.0);
		setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));

		setMinWidth(MeteorConstants.PANEL_WIDTH);
		setMaxWidth(MeteorConstants.PANEL_WIDTH);

		FontAwesomeIconView panelButtonIcon = new FontAwesomeIconView(FontAwesomeIcon.PLUG);
		AnchorPane.setTopAnchor(panelButtonIcon, 12.0);
		AnchorPane.setLeftAnchor(panelButtonIcon, 80.0);
		panelButtonIcon.setFill(javafx.scene.paint.Color.AQUA);
		panelButtonIcon.setSize("18");

		pluginTitle = new Text();
		AnchorPane.setTopAnchor(pluginTitle, 8.0);
		AnchorPane.setLeftAnchor(pluginTitle, 104.0);
		pluginTitle.setFont(new Font(18));
		pluginTitle.setFill(javafx.scene.paint.Color.WHITE);
		pluginTitle.setText(plugin.getName());

		titlePanel.getChildren().addAll(panelButtonIcon, pluginTitle);

		configList = new VBox();
		configList.setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));

		ScrollPane scrollPane = new ScrollPane();
		AnchorPane.setLeftAnchor(scrollPane, 4.0);
		AnchorPane.setRightAnchor(scrollPane, 4.0);
		AnchorPane.setBottomAnchor(scrollPane, 0.0);
		AnchorPane.setTopAnchor(scrollPane, 40.0);
		scrollPane.getStylesheets().add("css/plugins/jfx-scrollbar.css");
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));
		scrollPane.setContent(configList);

		getChildren().addAll(titlePanel, scrollPane);

		if (plugin.isToggleable()) {
			toggleButton = new PluginToggleButton(plugin);
			eventBus.register(toggleButton);
			toggleButton.setSelected(plugin.enabled);
			AnchorPane.setTopAnchor(toggleButton, 15.0);
			AnchorPane.setBottomAnchor(toggleButton, 2.0);
			AnchorPane.setRightAnchor(toggleButton, 8.0);
			toggleButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> plugin.toggle());
			titlePanel.getChildren().add(toggleButton);
		}

		backButton.setTextFill(MeteorConstants.CYAN_PAINT);
		backButton.setButtonType(JFXButton.ButtonType.RAISED);
		backButton.setBackground(new Background(new BackgroundFill(MeteorConstants.GRAY, null, null)));

		FontAwesomeIconView graphic = new FontAwesomeIconView(FontAwesomeIcon.ARROW_LEFT);
		graphic.setFill(Paint.valueOf("CYAN"));
		backButton.setGraphic(graphic);
		backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> meteorUI.showPlugins());
		AnchorPane.setLeftAnchor(backButton, 8.0);
		AnchorPane.setTopAnchor(backButton, 8.0);
		titlePanel.getChildren().add(backButton);
		rebuild();
	}

	private void rebuild() {
		sections.clear();
		configList.getChildren().clear();

		initSections();
		initConfigs();
	}

	private void initSections() {
		Config config = plugin.getConfig(configManager);
		ConfigDescriptor descriptor = configManager.getConfigDescriptor(config);

		for (ConfigSectionDescriptor csd : descriptor.getSections().stream()
				.sorted(Comparator.comparingInt(ConfigSectionDescriptor::position))
				.collect(Collectors.toList())) {
			ConfigSection section = csd.getSection();
			SectionPane sectionBox = createSection(section);
			configList.getChildren().add(sectionBox.getRootPane());
			if (openedCache.get(section.keyName()) != null) {
				sectionBox.getRootPane().setExpanded(openedCache.get(section.keyName()));
			} else {
				sectionBox.getRootPane().setExpanded(!section.closedByDefault());
			}
			sectionBox.getRootPane().expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
				openedCache.put(section.keyName(), isNowExpanded);
			});
		}
	}

	private void initConfigs() {
		Config config = plugin.getConfig(configManager);
		ConfigDescriptor descriptor = configManager.getConfigDescriptor(config);

		if (descriptor != null) {
			for (ConfigItemDescriptor configItemDescriptor : descriptor.getItems().stream()
					.sorted(Comparator.comparingInt(ConfigItemDescriptor::position))
					.collect(Collectors.toList())) {
				SectionPane sectionBox = sections.get(configItemDescriptor.getItem().section());
				Pane configContainer = sectionBox != null ? sectionBox.getContainer() : new AnchorPane();

				if (configItemDescriptor.getType() == int.class) {
					if (configItemDescriptor.getRange() != null) {
						if (configItemDescriptor.getRange().textInput()) {
							createIntegerTextNode(descriptor, configContainer, configItemDescriptor);
						} else {
							createIntegerSliderNode(descriptor, configContainer, configItemDescriptor);
						}
					} else {
						createIntegerSliderNode(descriptor, configContainer, configItemDescriptor);
					}
				}

				if (configItemDescriptor.getType() == boolean.class) {
					createBooleanNode(descriptor, configContainer, configItemDescriptor);
				}

				if (configItemDescriptor.getType() == String.class) {
					if (configItemDescriptor.getItem().textField()) {
						createStringNode(descriptor, configContainer, configItemDescriptor);
					} else {
						createStringAreaNode(descriptor, configContainer, configItemDescriptor);
					}
				}

				if (configItemDescriptor.getType() == Color.class) {
					createColorPickerNode(descriptor, configContainer, configItemDescriptor);
				}

				if (configItemDescriptor.getType() == double.class) {
					createdDoubleTextNode(descriptor, configContainer, configItemDescriptor);
				}

				if (configItemDescriptor.getType() == Button.class) {
					createButtonNode(descriptor, configContainer, configItemDescriptor);
				}

				if (configItemDescriptor.getType() == ModifierlessKeybind.class) {
					createHotKeyNode(descriptor, configContainer, configItemDescriptor);
				}

				if (configItemDescriptor.getType() == Keybind.class) {
					createDefaultKeyBindNode(descriptor, configContainer, configItemDescriptor);
				}

				if (configItemDescriptor.getType().isEnum()) {
					createEnumNode(descriptor, configContainer, configItemDescriptor);
				}

				if (!configContainer.getChildren().isEmpty() && !configList.getChildren().contains(configContainer)) {
					configList.getChildren().add(configContainer);
				}
			}
		}
	}

	private void createButtonNode(ConfigDescriptor config, Pane root, ConfigItemDescriptor configItem) {
		JFXButton button;
		if (configItem.getIcon().canToggle()) {
			button = new ConfigToggleButton();
		} else {
			button = new ConfigButton(configItem.name(), configItem.getIcon().value());
		}

		AnchorPane.setTopAnchor(button, 4.0);
		AnchorPane.setBottomAnchor(button, 4.0);
		AnchorPane.setRightAnchor(button, 40.0);
		AnchorPane.setLeftAnchor(button, 40.0);

		button.autosize();
		button.setTextFill(MeteorConstants.CYAN_PAINT);
		button.setButtonType(JFXButton.ButtonType.RAISED);
		button.setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));


		button.pressedProperty().addListener((options, oldValue, pressed) -> {
			if (!pressed) {
				return;
			}

			if (button instanceof ConfigToggleButton cfg) {
				cfg.toggle();
			}

			client.getCallbacks().post(new ConfigButtonClicked(config.getGroup().value(), configItem.key()));
		});

		addConfigItemComponents(config, configItem, root, button);
	}

	private void createHotKeyNode(ConfigDescriptor config, Pane root, ConfigItemDescriptor configItem) {
		Text name = createText(configItem.name(), Paint.valueOf("WHITE"), configItem.getItem().description());

		ConfigButton button = new ConfigButton(configManager.getConfiguration(config.getGroup().value(), configItem.key(), ModifierlessKeybind.class).toString());
		AnchorPane.setTopAnchor(button, 4.0);
		AnchorPane.setBottomAnchor(button, 4.0);
		AnchorPane.setRightAnchor(button, 0.0);
		AnchorPane.setLeftAnchor(button, 190.0);

		button.autosize();
		button.setTextFill(MeteorConstants.CYAN_PAINT);
		button.setButtonType(JFXButton.ButtonType.RAISED);
		button.setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));

		button.pressedProperty().addListener((options, oldValue, pressed) -> {
			if (!pressed) {
				return;
			}

			button.setText("Press any key...");
			EventHandler<KeyEvent> keyListener = (e) -> {
				configManager.setConfiguration(config.getGroup().value(), configItem.key(), new ModifierlessKeybind(getExtendedKeyCodeForChar(e.getCharacter().charAt(0)),
						0));
				button.setText(e.getCharacter().toUpperCase());
			};
			EventHandler<KeyEvent> unregisterListener = (e) -> {
				button.removeEventHandler(KeyEvent.KEY_TYPED, keyListener);
			};
			button.addEventHandler(KeyEvent.KEY_TYPED, keyListener);
			button.addEventHandler(KeyEvent.KEY_TYPED, unregisterListener);
		});

		addConfigItemComponents(config, configItem, root, name, button);
	}

	private void createDefaultKeyBindNode(ConfigDescriptor config, Pane root, ConfigItemDescriptor configItem) {
		Text name = createText(configItem.name(), Paint.valueOf("WHITE"), configItem.getItem().description());

		ConfigButton button = new ConfigButton(configManager.getConfiguration(config.getGroup().value(), configItem.key(), Keybind.class).toString());
		AnchorPane.setTopAnchor(button, 4.0);
		AnchorPane.setBottomAnchor(button, 4.0);
		AnchorPane.setRightAnchor(button, 0.0);
		AnchorPane.setLeftAnchor(button, 190.0);

		button.autosize();
		button.setTextFill(MeteorConstants.CYAN_PAINT);
		button.setButtonType(JFXButton.ButtonType.RAISED);
		button.setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));

		button.pressedProperty().addListener((options, oldValue, pressed) -> {
			if (!pressed) {
				return;
			}

			button.setText("Press any key...");
			EventHandler<KeyEvent> keyListener = (e) -> {
				configManager.setConfiguration(config.getGroup().value(), configItem.key(), new ModifierlessKeybind(getExtendedKeyCodeForChar(e.getCharacter().charAt(0)),
						0));
				button.setText(e.getCharacter().toUpperCase());
			};
			EventHandler<KeyEvent> unregisterListener = (e) -> {
				button.removeEventHandler(KeyEvent.KEY_TYPED, keyListener);
			};
			button.addEventHandler(KeyEvent.KEY_TYPED, keyListener);
			button.addEventHandler(KeyEvent.KEY_TYPED, unregisterListener);
		});

		addConfigItemComponents(config, configItem, root, name, button);
	}

	private void createEnumNode(ConfigDescriptor config, Pane root, ConfigItemDescriptor configItem) {
		Text name = createText(configItem.name(), Paint.valueOf("WHITE"), configItem.getItem().description());

		Class<? extends Enum> type = (Class<? extends Enum>) configItem.getType();
		Enum<?> currentConfigEnum = Enum.valueOf(type, configManager.getConfiguration(config.getGroup().value(), configItem.key()));
		List<Enum<?>> list = new ArrayList<>();
		Enum<?> currentToSet = null;
		for (Enum<?> o : type.getEnumConstants()) {
			if (o.equals(currentConfigEnum)) {
				currentToSet = o;
			}
			list.add(o);
		}
		ObservableList<Enum<?>> observableList = new ObservableListWrapper<>(list);
		JFXComboBox<Enum<?>> comboBox = new JFXComboBox<>(observableList);
		comboBox.setValue(currentToSet);

		AnchorPane.setRightAnchor(comboBox, 8.0);
		AnchorPane.setTopAnchor(comboBox, 0.0);
		AnchorPane.setLeftAnchor(comboBox, 150.0);

		comboBox.getStylesheets().add("css/plugins/jfx-combobox.css");
		comboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			updateConfigItemValue(config, configItem, newValue.name());
		});

		addConfigItemComponents(config, configItem, root, name, comboBox);
	}

	private void createdDoubleTextNode(ConfigDescriptor config, Pane root, ConfigItemDescriptor configItem) {
		Text name = createText(configItem.name(), Paint.valueOf("WHITE"), configItem.getItem().description());

		JFXTextField textField = new JFXTextField();
		AnchorPane.setLeftAnchor(textField, 200.0);
		AnchorPane.setRightAnchor(textField, 8.0);

		textField.setFont(Font.font(MeteorLiteClientModule.METEOR_FONT_SIZE));
		textField.setText(configManager.getConfiguration(config.getGroup().value(), configItem.key(), String.class));
		textField.textProperty().addListener((obs, oldValue, newValue) -> {
			double inputValue = checkDoubleInput(configItem, newValue);
			if (inputValue != Double.MIN_VALUE) {
				updateConfigItemValue(config, configItem, inputValue);
			}
		});

		textField.getStylesheets().add("css/plugins/jfx-textfield.css");

		addConfigItemComponents(config, configItem, root, name, textField);
	}

	private void createStringAreaNode(ConfigDescriptor config, Pane root, ConfigItemDescriptor descriptor) {
		Text name = createText(descriptor.name(), Paint.valueOf("WHITE"), descriptor.getItem().description());

		JFXTextArea textArea = new JFXTextArea();
		AnchorPane.setLeftAnchor(textArea, 0.0);
		AnchorPane.setRightAnchor(textArea, 0.0);
		AnchorPane.setTopAnchor(textArea, 24.0);
		AnchorPane.setBottomAnchor(textArea, 8.0);
		textArea.setFont(Font.font(MeteorLiteClientModule.METEOR_FONT_SIZE));

		textArea.setWrapText(true);
		textArea.setText(configManager.getConfiguration(config.getGroup().value(), descriptor.key(), String.class));
		textArea.getStylesheets().add("css/plugins/jfx-textarea.css");
		textArea.setStyle("-jfx-focus-color: CYAN;");
		textArea.textProperty().addListener((observable, oldValue, newValue) -> updateConfigItemValue(config, descriptor, newValue));

		addConfigItemComponents(config, descriptor, root, name, textArea);
	}

	private void createStringNode(ConfigDescriptor config, Pane root, ConfigItemDescriptor descriptor) {
		Text name = createText(descriptor.name(), Paint.valueOf("WHITE"), descriptor.getItem().description());

		TextField textfield = descriptor.getItem().secret() ? new JFXPasswordField() : new JFXTextField();
		AnchorPane.setLeftAnchor(textfield, 0.0);
		AnchorPane.setRightAnchor(textfield, 0.0);
		AnchorPane.setTopAnchor(textfield, 34.0);
		AnchorPane.setBottomAnchor(textfield, 8.0);
		textfield.setFont(Font.font(MeteorLiteClientModule.METEOR_FONT_SIZE));

		textfield.setText(configManager.getConfiguration(config.getGroup().value(), descriptor.key(), String.class));
		textfield.getStylesheets().add("css/plugins/jfx-textfield.css");
		textfield.setStyle("-jfx-focus-color: CYAN;");
		textfield.textProperty().addListener((observable, oldValue, newValue) -> updateConfigItemValue(config, descriptor, newValue));

		addConfigItemComponents(config, descriptor, root, name, textfield);
	}

	private void createBooleanNode(ConfigDescriptor config, Pane root, ConfigItemDescriptor descriptor) {
		Text name = createText(descriptor.name(), Paint.valueOf("WHITE"), descriptor.getItem().description());

		PluginToggleButton toggleButton = new PluginToggleButton(plugin);
		AnchorPane.setTopAnchor(toggleButton, 2.0);
		AnchorPane.setBottomAnchor(toggleButton, 2.0);
		AnchorPane.setRightAnchor(toggleButton, 8.0);

		Object o = configManager.getConfiguration(config.getGroup().value(), descriptor.key(), descriptor.getType());
		boolean enabled = false;
		if (o instanceof Boolean) {
			enabled = (Boolean) o;
		}
		toggleButton.setSelected(enabled);
		toggleButton.setStyle("-fx-text-fill: CYAN;");
		toggleButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
			updateConfigItemValue(config, descriptor, toggleButton.isSelected());
			if (hidesOtherConfigs(descriptor)) {
				rebuild();
			}
		});

		addConfigItemComponents(config, descriptor, root, name, toggleButton);
	}

	private void createIntegerSliderNode(ConfigDescriptor config, Pane root, ConfigItemDescriptor descriptor) {
		Text name = createText(descriptor.name(), Paint.valueOf("WHITE"), descriptor.getItem().description());

		JFXSlider slider = new JFXSlider();
		AnchorPane.setLeftAnchor(slider, 0.0);
		AnchorPane.setTopAnchor(slider, 34.0);
		AnchorPane.setRightAnchor(slider, 8.0);

		int min = 0;
		int max = Integer.MAX_VALUE;
		if (descriptor.getRange() != null) {
			min = descriptor.getRange().min();
			max = descriptor.getRange().max();
		}

		slider.setValue(configManager.getConfiguration(config.getGroup().value(), descriptor.key(), Double.class));
		slider.setMax(max);
		slider.setMin(min);
		slider.setMajorTickUnit(max / 4.0);
		slider.setMinorTickCount(2);
		slider.getStylesheets().add("css/plugins/jfx-slider.css");
		slider.setShowTickMarks(true);
		slider.setShowTickLabels(true);
		slider.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> updateConfigItemValue(config, descriptor, (int) slider.getValue()));

		addConfigItemComponents(config, descriptor, root, name, slider);
	}

	private void createIntegerTextNode(ConfigDescriptor config, Pane root, ConfigItemDescriptor descriptor) {
		Text name = createText(descriptor.name(), Paint.valueOf("WHITE"), descriptor.getItem().description());

		JFXTextField textField = new JFXTextField();
		AnchorPane.setTopAnchor(textField, 1.0);
		//		AnchorPane.setBottomAnchor(textField, 1.0);
		AnchorPane.setLeftAnchor(textField, 200.0);
		AnchorPane.setRightAnchor(textField, 8.0);

		textField.setFont(Font.font(MeteorLiteClientModule.METEOR_FONT_SIZE));
		textField.setText(configManager.getConfiguration(config.getGroup().value(), descriptor.key(), String.class));
		textField.textProperty().addListener((obs, oldValue, newValue) -> {
			int inputValue = checkIntInput(descriptor, newValue);
			if (inputValue != Integer.MIN_VALUE) {
				updateConfigItemValue(config, descriptor, inputValue);
			}
		});

		textField.setStyle("-jfx-focus-color: CYAN;");
		textField.getStylesheets().add("css/plugins/jfx-textfield.css");

		addConfigItemComponents(config, descriptor, root, name, textField);
	}

	private void createColorPickerNode(ConfigDescriptor config, Pane root, ConfigItemDescriptor configItem) {
		Text name = createText(configItem.name(), Paint.valueOf("WHITE"), configItem.getItem().description());

		JFXColorPicker colorPicker = new JFXColorPicker();
		AnchorPane.setTopAnchor(colorPicker, 1.0);
		AnchorPane.setBottomAnchor(colorPicker, 1.0);
		AnchorPane.setLeftAnchor(colorPicker, 200.0);
		AnchorPane.setRightAnchor(colorPicker, 8.0);

		Color c = configManager.getConfiguration(config.getGroup().value(), configItem.key(), Color.class);
		if (c == null) {
			logger.warn(config.getGroup().value() + ":" + configItem.key() + " color can't be null");
			c = Color.WHITE;
		}
		double r = c.getRed() / 255.0;
		double g = c.getGreen() / 255.0;
		double b = c.getBlue() / 255.0;
		double a = c.getAlpha() / 255.0;
		colorPicker.setValue(javafx.scene.paint.Color.color(r, g, b, a));
		colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			double nr = newValue.getRed() * 255.0;
			double ng = newValue.getGreen() * 255.0;
			double nb = newValue.getBlue() * 255.0;
			double na = newValue.getOpacity() * 255.0;
			Color colorToSet = new Color((int) nr, (int) ng, (int) nb, (int) na);
			updateConfigItemValue(config, configItem, colorToSet);
		});

		addConfigItemComponents(config, configItem, root, name, colorPicker);
	}

	private int checkIntInput(ConfigItemDescriptor descriptor, String input) {
		if (input == null || input.isBlank()) {
			return Integer.MIN_VALUE;
		}

		int i;
		try {
			i = Integer.parseInt(input);
		} catch (Exception e) {
			return Integer.MIN_VALUE;
		}

		Range range = descriptor.getRange();
		if (range == null) {
			return i;
		}

		if (i >= range.min() && i <= range.max()) {
			return i;
		}

		return Integer.MIN_VALUE;
	}

	private double checkDoubleInput(ConfigItemDescriptor descriptor, String input) {
		if (input == null || input.isBlank()) {
			return Double.MIN_VALUE;
		}

		double i;
		try {
			i = Double.parseDouble(input);
		} catch (Exception e) {
			return Double.MIN_VALUE;
		}

		Range range = descriptor.getRange();
		if (range == null) {
			return i;
		}
		if (i >= range.min() && i <= range.max()) {
			return i;
		}

		return Double.MIN_VALUE;
	}

	private void updateConfigItemValue(ConfigDescriptor config, ConfigItemDescriptor configItem, Object value) {
		configManager.setConfiguration(config.getGroup().value(), configItem.key(), value);
		plugin.updateConfig();
	}

	public SectionPane createSection(ConfigSection configSection) {
		SectionPane section = new SectionPane(configSection.name());
		sections.put(section.getName(), section);
		return section;
	}

	private void addConfigItemComponents(ConfigDescriptor cd, ConfigItemDescriptor cid, Pane root, Node... nodes) {
		if (root instanceof VBox) {
			Pane pane = new AnchorPane();
			pane.getChildren().addAll(nodes);
			if (!hideUnhide(cd, cid)) {
				return;
			}

			root.getChildren().add(pane);
		} else {
			if (!hideUnhide(cd, cid)) {
				return;
			}

			root.getChildren().addAll(nodes);
		}
	}

	private MeteorText createText(String text, Paint color, String tooltipText) {
		MeteorText label = new MeteorText(text);
		AnchorPane.setTopAnchor(label, 3.0);
		AnchorPane.setBottomAnchor(label, 3.0);
		AnchorPane.setLeftAnchor(label, 4.0);
		label.setFill(color);

		if (tooltipText != null) {
			JFXTooltip tooltip = new JFXTooltip();
			tooltip.setText(tooltipText);

			label.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
				if (!tooltip.isShowing() && tooltip.getText().length() > 0) {
					tooltip.showOnAnchors(label, 0, -50);
				}
			});

			label.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
				if (tooltip.isShowing()) {
					tooltip.hide();
				}
			});
		}

		return label;
	}

	private boolean hidesOtherConfigs(ConfigItemDescriptor cid) {
		Config config = plugin.getConfig(configManager);
		ConfigDescriptor descriptor = configManager.getConfigDescriptor(config);
		for (ConfigItemDescriptor item : descriptor.getItems()) {
			if (item.getItem().hide().contains(cid.key()) || item.getItem().unhide().contains(cid.key())) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private boolean hideUnhide(ConfigDescriptor cd, ConfigItemDescriptor cid) {
		boolean unhide = cid.getItem().hidden();
		boolean hide = !cid.getItem().hide().isEmpty();

		if (unhide || hide) {
			boolean show = false;

			List<String> itemHide = Splitter
					.onPattern("\\|\\|")
					.trimResults()
					.omitEmptyStrings()
					.splitToList(String.format("%s || %s", cid.getItem().unhide(), cid.getItem().hide()));

			for (ConfigItemDescriptor cid2 : cd.getItems()) {
				if (itemHide.contains(cid2.getItem().keyName())) {
					if (cid2.getType() == boolean.class) {
						show = Boolean.parseBoolean(configManager.getConfiguration(cd.getGroup().value(), cid2.getItem().keyName()));
					} else if (cid2.getType().isEnum()) {
						Class<? extends Enum> type = (Class<? extends Enum>) cid2.getType();
						try {
							Enum selectedItem = Enum.valueOf(type, configManager.getConfiguration(cd.getGroup().value(), cid2.getItem().keyName()));
							if (!cid.getItem().unhideValue().equals("")) {
								List<String> unhideValue = Splitter
										.onPattern("\\|\\|")
										.trimResults()
										.omitEmptyStrings()
										.splitToList(cid.getItem().unhideValue());

								show = unhideValue.contains(selectedItem.toString());
							} else if (!cid.getItem().hideValue().equals("")) {
								List<String> hideValue = Splitter
										.onPattern("\\|\\|")
										.trimResults()
										.omitEmptyStrings()
										.splitToList(cid.getItem().hideValue());

								show = !hideValue.contains(selectedItem.toString());
							}
						} catch (IllegalArgumentException ignored) {
						}
					}
				}
			}

			return (!unhide || show) && (!hide || !show);
		}

		return true;
	}
}
