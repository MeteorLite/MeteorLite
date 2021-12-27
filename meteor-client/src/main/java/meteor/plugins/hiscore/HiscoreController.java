package meteor.plugins.hiscore;

import com.google.common.base.Strings;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javax.inject.Inject;
import meteor.MeteorLiteClientLauncher;
import meteor.ui.components.ToolbarButton;
import net.runelite.http.api.hiscore.HiscoreClient;
import net.runelite.http.api.hiscore.HiscoreEndpoint;
import okhttp3.OkHttpClient;
import meteor.Logger;

public class HiscoreController {

  @FXML
  AnchorPane hiscorePanel;

  private HiscoreClient hiscoreClient;

  @Inject
  private OkHttpClient okHttpClient;

  public static ArrayList<ToolbarButton> buttons = new ArrayList<>();
  public static AnchorPane staticAP;
  public static JFXTextField searchBox;
  private static final int MAX_USERNAME_LENGTH = 12;
  private boolean loading = false;
  public static HiscoreEndpoint endpoint;
  public static HiscoreController INSTANCE;
  private JFXButton attackButton;
  private JFXButton strengthButton;
  private JFXButton hitpointsButton;
  private JFXButton miningButton;
  private JFXButton agilityButton;
  private JFXButton smithingButton;
  private JFXButton defenceButton;
  private JFXButton rangedButton;
  private JFXButton prayerButton;
  private JFXButton magicButton;
  private JFXButton runecraftButton;
  private JFXButton constructionButton;
  private JFXButton herbloreButton;
  private JFXButton thievingButton;
  private JFXButton craftingButton;
  private JFXButton fletchingButton;
  private JFXButton slayerButton;
  private JFXButton hunterButton;
  private JFXButton fishingButton;
  private JFXButton cookingButton;
  private JFXButton firemakingButton;
  private JFXButton woodcuttingButton;
  private JFXButton farmingButton;
  private JFXButton overallButton;
  private JFXButton normalTableButton;
  private JFXButton ironmanTableButton;
  private JFXButton hcTableButton;
  private JFXButton ultTableButton;
  private JFXButton deadmanTableButton;
  private JFXButton leagueTableButton;

  @FXML
  protected void initialize() {
    MeteorLiteClientLauncher.injector.injectMembers(this);
    staticAP = hiscorePanel;
    hiscoreClient = new HiscoreClient(okHttpClient);
    INSTANCE = this;

    Text panelName = new Text("Hiscore Lookup");
    panelName.setFill(Paint.valueOf("CYAN"));
    AnchorPane.setTopAnchor(panelName, 10.0);
    AnchorPane.setLeftAnchor(panelName, 130.0);
    hiscorePanel.getChildren().add(panelName);

    searchBox = new JFXTextField();
    searchBox.getStylesheets().add("css/plugins/jfx-textfield.css");
    AnchorPane.setTopAnchor(searchBox, 30.0);
    AnchorPane.setLeftAnchor(searchBox, 5.0);
    AnchorPane.setRightAnchor(searchBox, 50.0);

    hiscorePanel.getChildren().add(searchBox);

    JFXButton searchButton = new JFXButton();
    searchButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(searchButton, 30.0);
    AnchorPane.setLeftAnchor(searchButton, 300.0);
    AnchorPane.setRightAnchor(searchButton, 5.0);
    FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.SEARCH);
    icon.setFill(Paint.valueOf("CYAN"));
    searchButton.setGraphic(icon);
    searchButton.pressedProperty().addListener((options, oldValue, pressed) -> {
      if (!pressed) {
        return;
      }

      if (!loading) {
        lookup();
      }
    });
    hiscorePanel.getChildren().add(searchButton);

    setupTableButtons();
    setupSkillButtons();
  }

  private void setupTableButtons() {
    normalTableButton = new JFXButton();
    normalTableButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(normalTableButton, 60.0);
    AnchorPane.setLeftAnchor(normalTableButton, 35.0);
    Image img = new Image("skill_icons_small/normal.png");
    ImageView view = new ImageView(img);
    normalTableButton.setGraphic(view);
    normalTableButton.pressedProperty().addListener((options, oldValue, pressed) -> {
      if (!pressed) {
        return;
      }
      endpoint = HiscoreEndpoint.NORMAL;

      if (!loading)
      lookup();
    });
    hiscorePanel.getChildren().add(normalTableButton);

    ironmanTableButton = new JFXButton();
    ironmanTableButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(ironmanTableButton, 60.0);
    AnchorPane.setLeftAnchor(ironmanTableButton, 82.0);
    Image ironmanimg = new Image("skill_icons_small/ironman.png");
    ImageView ironmanview = new ImageView(ironmanimg);
    ironmanTableButton.setGraphic(ironmanview);
    ironmanTableButton.pressedProperty().addListener((options, oldValue, pressed) -> {
      if (!pressed) {
        return;
      }
      endpoint = HiscoreEndpoint.IRONMAN;

      if (!loading)
      lookup();
    });
    hiscorePanel.getChildren().add(ironmanTableButton);

    hcTableButton = new JFXButton();
    hcTableButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(hcTableButton, 60.0);
    AnchorPane.setLeftAnchor(hcTableButton, 129.0);
    Image hcTableimg = new Image("skill_icons_small/hardcore_ironman.png");
    ImageView hcTableView = new ImageView(hcTableimg);
    hcTableButton.setGraphic(hcTableView);
    hcTableButton.pressedProperty().addListener((options, oldValue, pressed) -> {
      if (!pressed) {
        return;
      }
      endpoint = HiscoreEndpoint.HARDCORE_IRONMAN;

      if (!loading)
      lookup();
    });
    hiscorePanel.getChildren().add(hcTableButton);

    ultTableButton = new JFXButton();
    ultTableButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(ultTableButton, 60.0);
    AnchorPane.setLeftAnchor(ultTableButton, 176.0);
    Image ultTableimg = new Image("skill_icons_small/ultimate_ironman.png");
    ImageView ultTableView = new ImageView(ultTableimg);
    ultTableButton.setGraphic(ultTableView);
    ultTableButton.pressedProperty().addListener((options, oldValue, pressed) -> {
      if (!pressed) {
        return;
      }
      endpoint = HiscoreEndpoint.ULTIMATE_IRONMAN;

      if (!loading)
      lookup();
    });
    hiscorePanel.getChildren().add(ultTableButton);

    deadmanTableButton = new JFXButton();
    deadmanTableButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(deadmanTableButton, 60.0);
    AnchorPane.setLeftAnchor(deadmanTableButton, 223.0);
    Image deadmanTableimg = new Image("skill_icons_small/deadman.png");
    ImageView deadmanTableView = new ImageView(deadmanTableimg);
    deadmanTableButton.setGraphic(deadmanTableView);
    deadmanTableButton.pressedProperty().addListener((options, oldValue, pressed) -> {
      if (!pressed) {
        return;
      }
      endpoint = HiscoreEndpoint.DEADMAN;

      if (!loading)
      lookup();
    });
    hiscorePanel.getChildren().add(deadmanTableButton);

    leagueTableButton = new JFXButton();
    leagueTableButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(leagueTableButton, 60.0);
    AnchorPane.setLeftAnchor(leagueTableButton, 270.0);
    Image leagueTableimg = new Image("skill_icons_small/league.png");
    ImageView leagueTableView = new ImageView(leagueTableimg);
    leagueTableButton.setGraphic(leagueTableView);
    leagueTableButton.pressedProperty().addListener((options, oldValue, pressed) -> {
      if (!pressed) {
        return;
      }

      endpoint = HiscoreEndpoint.LEAGUE;
      if (!loading)
      lookup();
    });
    hiscorePanel.getChildren().add(leagueTableButton);
  }

  private void setupSkillButtons() {
    if (attackButton != null)
      return;

    attackButton = new JFXButton();
    attackButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(attackButton, 90.0);
    AnchorPane.setLeftAnchor(attackButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(attackButton, getRightAnchor(1));
    Image img = new Image("skill_icons_small/attack.png");
    ImageView view = new ImageView(img);
    attackButton.setGraphic(view);
    attackButton.setVisible(false);
    hiscorePanel.getChildren().add(attackButton);

    strengthButton = new JFXButton();
    strengthButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(strengthButton, getTopAnchor(2));
    AnchorPane.setLeftAnchor(strengthButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(strengthButton, getRightAnchor(1));
    Image strengthButtonImg = new Image("skill_icons_small/strength.png");
    ImageView strengthButtonView = new ImageView(strengthButtonImg);
    strengthButton.setGraphic(strengthButtonView);
    strengthButton.setVisible(false);
    hiscorePanel.getChildren().add(strengthButton);

    defenceButton = new JFXButton();
    defenceButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(defenceButton, getTopAnchor(3));
    AnchorPane.setLeftAnchor(defenceButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(defenceButton, getRightAnchor(1));
    Image defenceButtonImg = new Image("skill_icons_small/defence.png");
    ImageView defenceButtonView = new ImageView(defenceButtonImg);
    defenceButton.setGraphic(defenceButtonView);
    defenceButton.setVisible(false);
    hiscorePanel.getChildren().add(defenceButton);

    rangedButton = new JFXButton();
    rangedButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(rangedButton, getTopAnchor(4));
    AnchorPane.setLeftAnchor(rangedButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(rangedButton, getRightAnchor(1));
    Image rangedButtonImg = new Image("skill_icons_small/ranged.png");
    ImageView rangedButtonView = new ImageView(rangedButtonImg);
    rangedButton.setGraphic(rangedButtonView);
    rangedButton.setVisible(false);
    hiscorePanel.getChildren().add(rangedButton);

    prayerButton = new JFXButton();
    prayerButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(prayerButton, getTopAnchor(5));
    AnchorPane.setLeftAnchor(prayerButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(prayerButton, getRightAnchor(1));
    Image prayerButtonImg = new Image("skill_icons_small/prayer.png");
    ImageView prayerButtonView = new ImageView(prayerButtonImg);
    prayerButton.setGraphic(prayerButtonView);
    prayerButton.setVisible(false);
    hiscorePanel.getChildren().add(prayerButton);

    magicButton = new JFXButton();
    magicButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(magicButton, getTopAnchor(6));
    AnchorPane.setLeftAnchor(magicButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(magicButton, getRightAnchor(1));
    Image magicButtonImg = new Image("skill_icons_small/magic.png");
    ImageView magicButtonView = new ImageView(magicButtonImg);
    magicButton.setGraphic(magicButtonView);
    magicButton.setVisible(false);
    hiscorePanel.getChildren().add(magicButton);

    runecraftButton = new JFXButton();
    runecraftButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(runecraftButton, getTopAnchor(7));
    AnchorPane.setLeftAnchor(runecraftButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(runecraftButton, getRightAnchor(1));
    Image runecraftButtonImg = new Image("skill_icons_small/runecraft.png");
    ImageView runecraftButtonView = new ImageView(runecraftButtonImg);
    runecraftButton.setGraphic(runecraftButtonView);
    runecraftButton.setVisible(false);
    hiscorePanel.getChildren().add(runecraftButton);

    constructionButton = new JFXButton();
    constructionButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(constructionButton, getTopAnchor(8));
    AnchorPane.setLeftAnchor(constructionButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(constructionButton, getRightAnchor(1));
    Image constructionButtonImg = new Image("skill_icons_small/construction.png");
    ImageView constructionButtonView = new ImageView(constructionButtonImg);
    constructionButton.setGraphic(constructionButtonView);
    constructionButton.setVisible(false);
    hiscorePanel.getChildren().add(constructionButton);

    hitpointsButton = new JFXButton();
    hitpointsButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(hitpointsButton, getTopAnchor(1));
    AnchorPane.setLeftAnchor(hitpointsButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(hitpointsButton, getRightAnchor(2));
    Image hitpointsButtonImg = new Image("skill_icons_small/hitpoints.png");
    ImageView hitpointsButtonView = new ImageView(hitpointsButtonImg);
    hitpointsButton.setGraphic(hitpointsButtonView);
    hitpointsButton.setVisible(false);
    hiscorePanel.getChildren().add(hitpointsButton);

    agilityButton = new JFXButton();
    agilityButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(agilityButton, getTopAnchor(2));
    AnchorPane.setLeftAnchor(agilityButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(agilityButton, getRightAnchor(2));
    Image agilityButtonImg = new Image("skill_icons_small/agility.png");
    ImageView agilityButtonView = new ImageView(agilityButtonImg);
    agilityButton.setGraphic(agilityButtonView);
    agilityButton.setVisible(false);
    hiscorePanel.getChildren().add(agilityButton);

    herbloreButton = new JFXButton();
    herbloreButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(herbloreButton, getTopAnchor(3));
    AnchorPane.setLeftAnchor(herbloreButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(herbloreButton, getRightAnchor(2));
    Image herbloreButtonImg = new Image("skill_icons_small/herblore.png");
    ImageView herbloreButtonView = new ImageView(herbloreButtonImg);
    herbloreButton.setGraphic(herbloreButtonView);
    herbloreButton.setVisible(false);
    hiscorePanel.getChildren().add(herbloreButton);

    thievingButton = new JFXButton();
    thievingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(thievingButton, getTopAnchor(4));
    AnchorPane.setLeftAnchor(thievingButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(thievingButton, getRightAnchor(2));
    Image thievingButtonImg = new Image("skill_icons_small/thieving.png");
    ImageView thievingButtonView = new ImageView(thievingButtonImg);
    thievingButton.setGraphic(thievingButtonView);
    thievingButton.setVisible(false);
    hiscorePanel.getChildren().add(thievingButton);

    craftingButton = new JFXButton();
    craftingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(craftingButton, getTopAnchor(5));
    AnchorPane.setLeftAnchor(craftingButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(craftingButton, getRightAnchor(2));
    Image craftingButtonImg = new Image("skill_icons_small/crafting.png");
    ImageView craftingButtonView = new ImageView(craftingButtonImg);
    craftingButton.setGraphic(craftingButtonView);
    craftingButton.setVisible(false);
    hiscorePanel.getChildren().add(craftingButton);

    fletchingButton = new JFXButton();
    fletchingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(fletchingButton, getTopAnchor(6));
    AnchorPane.setLeftAnchor(fletchingButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(fletchingButton, getRightAnchor(2));
    Image fletchingButtonImg = new Image("skill_icons_small/fletching.png");
    ImageView fletchingButtonView = new ImageView(fletchingButtonImg);
    fletchingButton.setGraphic(fletchingButtonView);
    fletchingButton.setVisible(false);
    hiscorePanel.getChildren().add(fletchingButton);

    slayerButton = new JFXButton();
    slayerButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(slayerButton, getTopAnchor(7));
    AnchorPane.setLeftAnchor(slayerButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(slayerButton, getRightAnchor(2));
    Image slayerButtonImg = new Image("skill_icons_small/slayer.png");
    ImageView slayerButtonView = new ImageView(slayerButtonImg);
    slayerButton.setGraphic(slayerButtonView);
    slayerButton.setVisible(false);
    hiscorePanel.getChildren().add(slayerButton);

    hunterButton = new JFXButton();
    hunterButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(hunterButton, getTopAnchor(8));
    AnchorPane.setLeftAnchor(hunterButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(hunterButton, getRightAnchor(2));
    Image hunterButtonImg = new Image("skill_icons_small/hunter.png");
    ImageView hunterButtonView = new ImageView(hunterButtonImg);
    hunterButton.setGraphic(hunterButtonView);
    hunterButton.setVisible(false);
    hiscorePanel.getChildren().add(hunterButton);

    miningButton = new JFXButton();
    miningButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(miningButton, getTopAnchor(1));
    AnchorPane.setLeftAnchor(miningButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(miningButton, getRightAnchor(3));
    Image miningButtonImg = new Image("skill_icons_small/mining.png");
    ImageView miningButtonView = new ImageView(miningButtonImg);
    miningButton.setGraphic(miningButtonView);
    miningButton.setVisible(false);
    hiscorePanel.getChildren().add(miningButton);

    smithingButton = new JFXButton();
    smithingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(smithingButton, getTopAnchor(2));
    AnchorPane.setLeftAnchor(smithingButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(smithingButton, getRightAnchor(3));
    Image smithingButtonImg = new Image("skill_icons_small/smithing.png");
    ImageView smithingButtonView = new ImageView(smithingButtonImg);
    smithingButton.setGraphic(smithingButtonView);
    smithingButton.setVisible(false);
    hiscorePanel.getChildren().add(smithingButton);

    fishingButton = new JFXButton();
    fishingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(fishingButton, getTopAnchor(3));
    AnchorPane.setLeftAnchor(fishingButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(fishingButton, getRightAnchor(3));
    Image fishingButtonImg = new Image("skill_icons_small/fishing.png");
    ImageView fishingButtonView = new ImageView(fishingButtonImg);
    fishingButton.setGraphic(fishingButtonView);
    fishingButton.setVisible(false);
    hiscorePanel.getChildren().add(fishingButton);

    cookingButton = new JFXButton();
    cookingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(cookingButton, getTopAnchor(4));
    AnchorPane.setLeftAnchor(cookingButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(cookingButton, getRightAnchor(3));
    Image cookingButtonImg = new Image("skill_icons_small/cooking.png");
    ImageView cookingButtonView = new ImageView(cookingButtonImg);
    cookingButton.setGraphic(cookingButtonView);
    cookingButton.setVisible(false);
    hiscorePanel.getChildren().add(cookingButton);

    firemakingButton = new JFXButton();
    firemakingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(firemakingButton, getTopAnchor(5));
    AnchorPane.setLeftAnchor(firemakingButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(firemakingButton, getRightAnchor(3));
    Image firemakingButtonImg = new Image("skill_icons_small/firemaking.png");
    ImageView firemakingButtonView = new ImageView(firemakingButtonImg);
    firemakingButton.setGraphic(firemakingButtonView);
    firemakingButton.setVisible(false);
    hiscorePanel.getChildren().add(firemakingButton);

    woodcuttingButton = new JFXButton();
    woodcuttingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(woodcuttingButton, getTopAnchor(6));
    AnchorPane.setLeftAnchor(woodcuttingButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(woodcuttingButton, getRightAnchor(3));
    Image woodcuttingButtonImg = new Image("skill_icons_small/woodcutting.png");
    ImageView woodcuttingButtonView = new ImageView(woodcuttingButtonImg);
    woodcuttingButton.setGraphic(woodcuttingButtonView);
    woodcuttingButton.setVisible(false);
    hiscorePanel.getChildren().add(woodcuttingButton);

    farmingButton = new JFXButton();
    farmingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(farmingButton, getTopAnchor(7));
    AnchorPane.setLeftAnchor(farmingButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(farmingButton, getRightAnchor(3));
    Image farmingButtonImg = new Image("skill_icons_small/farming.png");
    ImageView farmingButtonView = new ImageView(farmingButtonImg);
    farmingButton.setGraphic(farmingButtonView);
    farmingButton.setVisible(false);
    hiscorePanel.getChildren().add(farmingButton);

    overallButton = new JFXButton();
    overallButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(overallButton, getTopAnchor(8));
    AnchorPane.setLeftAnchor(overallButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(overallButton, getRightAnchor(3));
    Image overallButtonImg = new Image("skill_icons_small/overall.png");
    ImageView overallButtonView = new ImageView(overallButtonImg);
    overallButton.setGraphic(overallButtonView);
    overallButton.setVisible(false);
    hiscorePanel.getChildren().add(overallButton);
  }

  private double getTopAnchor(int row) {
    return 90.0 + ((row - 1) * 35);
  }

  private double getLeftAnchor(int column) {
    return switch (column) {
      case 3 -> 230;
      case 2 -> 140;
      case 1 -> 30;
      default -> -1;
    };
  }

  private double getRightAnchor(int column) {
    return switch (column) {
      case 3 -> 35;
      case 2 -> 130;
      case 1 -> 225;
      default -> -1;
    };
  }

  private static String sanitize(String lookup)
  {
    return lookup.replace('\u00A0', ' ');
  }

  public void lookup()
  {
    resetButtons();
    final String lookup = sanitize(searchBox.getText());

    if (Strings.isNullOrEmpty(lookup))
    {
      return;
    }

    /* RuneScape usernames can't be longer than 12 characters long */
    if (lookup.length() > MAX_USERNAME_LENGTH)
    {
      loading = false;
      return;
    }

    searchBox.setEditable(false);
    loading = true;

    // if for some reason no endpoint was selected, default to normal
    if (endpoint == null)
    {
      endpoint = HiscoreEndpoint.NORMAL;
    }

    hiscoreClient.lookupAsync(lookup, endpoint).whenCompleteAsync((result, ex) ->
        Platform.runLater(() ->
        {
          if (!sanitize(searchBox.getText()).equals(lookup))
          {
            // search has changed in the meantime
            return;
          }

          if (result == null || ex != null)
          {
            if (ex != null)
            {
              Logger.getLogger(getClass()).warn("Error fetching Hiscore data " + ex.getMessage());
            }

            searchBox.setEditable(true);
            loading = false;
            return;
          }

          //successful player search
          searchBox.setEditable(true);
          loading = false;

          attackButton.setText("  " + result.getAttack().getLevel());
          attackButton.setVisible(true);
          strengthButton.setText("  " + result.getStrength().getLevel());
          strengthButton.setVisible(true);
          defenceButton.setText("  " + result.getDefence().getLevel());
          defenceButton.setVisible(true);
          rangedButton.setText("  " + result.getRanged().getLevel());
          rangedButton.setVisible(true);
          prayerButton.setText("  " + result.getPrayer().getLevel());
          prayerButton.setVisible(true);
          magicButton.setText("  " + result.getMagic().getLevel());
          magicButton.setVisible(true);
          runecraftButton.setText("  " + result.getRunecraft().getLevel());
          runecraftButton.setVisible(true);
          constructionButton.setText("  " + result.getConstruction().getLevel());
          constructionButton.setVisible(true);
          hitpointsButton.setText("  " + result.getHitpoints().getLevel());
          hitpointsButton.setVisible(true);
          miningButton.setText("  " + result.getMining().getLevel());
          miningButton.setVisible(true);
          agilityButton.setText("  " + result.getAgility().getLevel());
          agilityButton.setVisible(true);
          herbloreButton.setText("  " + result.getHerblore().getLevel());
          herbloreButton.setVisible(true);
          thievingButton.setText("  " + result.getThieving().getLevel());
          thievingButton.setVisible(true);
          craftingButton.setText("  " + result.getCrafting().getLevel());
          craftingButton.setVisible(true);
          fletchingButton.setText("  " + result.getFletching().getLevel());
          fletchingButton.setVisible(true);
          slayerButton.setText("  " + result.getSlayer().getLevel());
          slayerButton.setVisible(true);
          hunterButton.setText("  " + result.getHunter().getLevel());
          hunterButton.setVisible(true);
          smithingButton.setText("  " + result.getSmithing().getLevel());
          smithingButton.setVisible(true);
          fishingButton.setText("  " + result.getFishing().getLevel());
          fishingButton.setVisible(true);
          cookingButton.setText("  " + result.getCooking().getLevel());
          cookingButton.setVisible(true);
          firemakingButton.setText("  " + result.getFiremaking().getLevel());
          firemakingButton.setVisible(true);
          woodcuttingButton.setText("  " + result.getWoodcutting().getLevel());
          woodcuttingButton.setVisible(true);
          farmingButton.setText("  " + result.getFarming().getLevel());
          farmingButton.setVisible(true);
          overallButton.setText("  " + result.getOverall().getLevel());
          overallButton.setVisible(true);
        }));
  }

  private void resetButtons() {
    attackButton.setText("");
    attackButton.setVisible(false);
    strengthButton.setText("");
    strengthButton.setVisible(false);
    defenceButton.setText("");
    defenceButton.setVisible(false);
    rangedButton.setText("");
    rangedButton.setVisible(false);
    prayerButton.setText("");
    prayerButton.setVisible(false);
    magicButton.setText("");
    magicButton.setVisible(false);
    runecraftButton.setText("");
    runecraftButton.setVisible(false);
    constructionButton.setText("");
    constructionButton.setVisible(false);
    hitpointsButton.setText("");
    hitpointsButton.setVisible(false);
    miningButton.setText("");
    miningButton.setVisible(false);
    agilityButton.setText("");
    agilityButton.setVisible(false);
    herbloreButton.setText("");
    herbloreButton.setVisible(false);
    thievingButton.setText("");
    thievingButton.setVisible(false);
    craftingButton.setText("");
    craftingButton.setVisible(false);
    fletchingButton.setText("");
    fletchingButton.setVisible(false);
    slayerButton.setText("");
    slayerButton.setVisible(false);
    hunterButton.setText("");
    hunterButton.setVisible(false);
    smithingButton.setText("");
    smithingButton.setVisible(false);
    fishingButton.setText("");
    fishingButton.setVisible(false);
    cookingButton.setText("");
    cookingButton.setVisible(false);
    firemakingButton.setText("");
    firemakingButton.setVisible(false);
    woodcuttingButton.setText("");
    woodcuttingButton.setVisible(false);
    farmingButton.setText("");
    farmingButton.setVisible(false);
    overallButton.setText("");
    overallButton.setVisible(false);
  }

}
