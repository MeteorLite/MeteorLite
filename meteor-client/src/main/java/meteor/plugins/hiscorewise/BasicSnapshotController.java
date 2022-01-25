package meteor.plugins.hiscorewise;

import static meteor.plugins.hiscorewise.HiscoreWisePlugin.skillOverviewPanel;
import com.google.common.base.Strings;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTooltip;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javax.inject.Inject;
import meteor.MeteorLiteClientLauncher;
import meteor.config.ConfigManager;
import meteor.ui.MeteorUI;
import meteor.ui.components.ToolbarButton;
import meteor.util.MeteorConstants;
import net.runelite.api.Skill;
import net.runelite.http.api.hiscore.HiscoreEndpoint;
import net.runelite.http.api.wiseoldman.WiseOldManClient;
import net.runelite.http.api.wiseoldman.model.BasicResponse;
import net.runelite.http.api.wiseoldman.model.KillSnapshot;
import net.runelite.http.api.wiseoldman.model.SkillSnapshot;
import org.sponge.util.Logger;

public class BasicSnapshotController {

  @FXML
  ScrollPane hiscorePanel;

  @Inject
  MeteorUI ui;

  @Inject
  ConfigManager configManager;

  private static AnchorPane skillPanel = new AnchorPane();
  private static VBox scrollPanel = new VBox();

  private WiseOldManClient wiseOldManClient;

  public static ArrayList<ToolbarButton> buttons = new ArrayList<>();
  public static ScrollPane staticAP;
  public static JFXTextField searchBox;
  private static final int MAX_USERNAME_LENGTH = 12;
  private boolean loading = false;
  public static HiscoreEndpoint endpoint;
  public static BasicSnapshotController INSTANCE;
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
  public static Skill lastSkillClicked;

  Text displayNameLabel;
  Text typeLabel;
  Text buildLabel;
  Text combatLvlLabel;
  Text ehpLabel;
  Text ehbLabel;
  Text ttmLabel;
  Text tt200mLabel;
  private String lastLookup = "";
  private BasicResponse lastResult;

  static DecimalFormat formatter = new DecimalFormat("#,###");
  private JFXButton abyssalSireButton;
  private JFXButton leaguePointsButton;
  private JFXButton bountyHunterHunterButton;
  private JFXButton bountyHunterRogueButton;
  private JFXButton lastManStandingButton;
  private JFXButton soulWarsZealButton;
  private JFXButton alchemicalHydraButton;
  private JFXButton barrowsChestsButton;
  private JFXButton bryophytaButton;
  private JFXButton callistoButton;
  private JFXButton cerberusButton;
  private JFXButton coxButton;
  private JFXButton coxChallengeButton;
  private JFXButton chaosElementalButton;
  private JFXButton chaosFanaticButton;
  private JFXButton commanderZilyanaButton;
  private JFXButton corpButton;
  private JFXButton crazyArchButton;
  private JFXButton dagannothPrimeButton;
  private JFXButton dagannothRexButton;
  private JFXButton dagannothSupremeButton;
  private JFXButton derangedArchButton;
  private JFXButton generalGraardorButton;
  private JFXButton giantMoleButton;
  private JFXButton grotesqueGuardiansButton;
  private JFXButton hesporiButton;
  private JFXButton kalphiteQueenButton;
  private JFXButton kbdButton;
  private JFXButton krakenButton;
  private JFXButton kreearraButton;
  private JFXButton krilTsutsarothButton;
  private JFXButton mimicButton;
  private JFXButton nightmareButton;
  private JFXButton phosanisNightmareButton;
  private JFXButton oborButton;
  private JFXButton sarachnisButton;
  private JFXButton scorpiaButton;
  private JFXButton skotizoButton;
  private JFXButton temporossButton;
  private JFXButton theGauntletButton;
  private JFXButton theCorruptedGauntletButton;
  private JFXButton tobButton;
  private JFXButton tobHardButton;
  private JFXButton thermieButton;
  private JFXButton zukButton;
  private JFXButton jadButton;
  private JFXButton venenatisButton;
  private JFXButton vetionButton;
  private JFXButton vorkathButton;
  private JFXButton wintertodtButton;
  private JFXButton zalcanoButton;
  private JFXButton zulrahButton;


  @FXML
  protected void initialize() {
    MeteorLiteClientLauncher.injector.injectMembers(this);
    staticAP = hiscorePanel;
    wiseOldManClient = new WiseOldManClient();
    INSTANCE = this;
    hiscorePanel.getStylesheets().add("css/plugins/jfx-scrollbar.css");
    skillPanel.setStyle("-fx-background-color: #252525;");
    scrollPanel.setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));
    scrollPanel.setFillWidth(true);
    scrollPanel.getChildren().clear();
    scrollPanel.getChildren().add(skillPanel);
    INSTANCE.hiscorePanel.setContent(scrollPanel);

    Text panelName = new Text("WiseOldMan Lookup");
    panelName.setFill(Paint.valueOf("CYAN"));
    AnchorPane.setTopAnchor(panelName, 10.0);
    AnchorPane.setLeftAnchor(panelName, 130.0);
    skillPanel.getChildren().add(panelName);

    searchBox = new JFXTextField();
    searchBox.getStylesheets().add("css/plugins/jfx-textfield.css");
    AnchorPane.setTopAnchor(searchBox, 30.0);
    AnchorPane.setLeftAnchor(searchBox, 5.0);
    AnchorPane.setRightAnchor(searchBox, 50.0);

    String lastSearch = configManager.getConfiguration("wiseTracker", "lastSearch");
    if (lastSearch != null)
      searchBox.setText(lastSearch);

    skillPanel.getChildren().add(searchBox);

    JFXButton searchButton = new JFXButton();
    searchButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(searchButton, 30.0);
    AnchorPane.setLeftAnchor(searchButton, 292.0);
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
    skillPanel.getChildren().add(searchButton);

    setupBasicInfo();
    setupSkillButtons();
  }

  private void setupBasicInfo() {
    displayNameLabel = new Text();
    displayNameLabel.setFill(Paint.valueOf("CYAN"));
    displayNameLabel.setLayoutX(5);
    displayNameLabel.setLayoutY(85);
    skillPanel.getChildren().add(displayNameLabel);

    typeLabel = new Text();
    typeLabel.setFill(Paint.valueOf("CYAN"));
    typeLabel.setLayoutX(5);
    typeLabel.setLayoutY(100);
    skillPanel.getChildren().add(typeLabel);

    buildLabel = new Text();
    buildLabel.setFill(Paint.valueOf("CYAN"));
    buildLabel.setLayoutX(5);
    buildLabel.setLayoutY(115);
    skillPanel.getChildren().add(buildLabel);

    combatLvlLabel = new Text();
    combatLvlLabel.setFill(Paint.valueOf("CYAN"));
    combatLvlLabel.setLayoutX(5);
    combatLvlLabel.setLayoutY(130);
    skillPanel.getChildren().add(combatLvlLabel);

    ehpLabel = new Text();
    ehpLabel.setFill(Paint.valueOf("CYAN"));
    ehpLabel.setLayoutX(5);
    ehpLabel.setLayoutY(145);
    skillPanel.getChildren().add(ehpLabel);

    ehbLabel = new Text();
    ehbLabel.setFill(Paint.valueOf("CYAN"));
    ehbLabel.setLayoutX(5);
    ehbLabel.setLayoutY(160);
    skillPanel.getChildren().add(ehbLabel);

    ttmLabel = new Text();
    ttmLabel.setFill(Paint.valueOf("CYAN"));
    ttmLabel.setLayoutX(5);
    ttmLabel.setLayoutY(175);
    skillPanel.getChildren().add(ttmLabel);

    tt200mLabel = new Text();
    tt200mLabel.setFill(Paint.valueOf("CYAN"));
    tt200mLabel.setLayoutX(5);
    tt200mLabel.setLayoutY(190);
    skillPanel.getChildren().add(tt200mLabel);
  }

  private void setupSkillButtons() {
    if (attackButton != null)
      return;

    attackButton = new JFXButton();
    attackButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(attackButton, getTopAnchor(1));
    AnchorPane.setLeftAnchor(attackButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(attackButton, getRightAnchor(1));
    Image img = new Image("skill_icons_small/attack.png");
    ImageView view = new ImageView(img);
    attackButton.setGraphic(view);
    attackButton.setVisible(false);
    attackButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.ATTACK);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(attackButton);

    strengthButton = new JFXButton();
    strengthButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(strengthButton, getTopAnchor(2));
    AnchorPane.setLeftAnchor(strengthButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(strengthButton, getRightAnchor(1));
    Image strengthButtonImg = new Image("skill_icons_small/strength.png");
    ImageView strengthButtonView = new ImageView(strengthButtonImg);
    strengthButton.setGraphic(strengthButtonView);
    strengthButton.setVisible(false);
    strengthButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.STRENGTH);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(strengthButton);

    defenceButton = new JFXButton();
    defenceButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(defenceButton, getTopAnchor(3));
    AnchorPane.setLeftAnchor(defenceButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(defenceButton, getRightAnchor(1));
    Image defenceButtonImg = new Image("skill_icons_small/defence.png");
    ImageView defenceButtonView = new ImageView(defenceButtonImg);
    defenceButton.setGraphic(defenceButtonView);
    defenceButton.setVisible(false);
    defenceButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.DEFENCE);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(defenceButton);

    rangedButton = new JFXButton();
    rangedButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(rangedButton, getTopAnchor(4));
    AnchorPane.setLeftAnchor(rangedButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(rangedButton, getRightAnchor(1));
    Image rangedButtonImg = new Image("skill_icons_small/ranged.png");
    ImageView rangedButtonView = new ImageView(rangedButtonImg);
    rangedButton.setGraphic(rangedButtonView);
    rangedButton.setVisible(false);
    rangedButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.RANGED);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(rangedButton);

    prayerButton = new JFXButton();
    prayerButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(prayerButton, getTopAnchor(5));
    AnchorPane.setLeftAnchor(prayerButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(prayerButton, getRightAnchor(1));
    Image prayerButtonImg = new Image("skill_icons_small/prayer.png");
    ImageView prayerButtonView = new ImageView(prayerButtonImg);
    prayerButton.setGraphic(prayerButtonView);
    prayerButton.setVisible(false);
    prayerButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.PRAYER);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(prayerButton);

    magicButton = new JFXButton();
    magicButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(magicButton, getTopAnchor(6));
    AnchorPane.setLeftAnchor(magicButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(magicButton, getRightAnchor(1));
    Image magicButtonImg = new Image("skill_icons_small/magic.png");
    ImageView magicButtonView = new ImageView(magicButtonImg);
    magicButton.setGraphic(magicButtonView);
    magicButton.setVisible(false);
    magicButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.MAGIC);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(magicButton);

    runecraftButton = new JFXButton();
    runecraftButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(runecraftButton, getTopAnchor(7));
    AnchorPane.setLeftAnchor(runecraftButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(runecraftButton, getRightAnchor(1));
    Image runecraftButtonImg = new Image("skill_icons_small/runecraft.png");
    ImageView runecraftButtonView = new ImageView(runecraftButtonImg);
    runecraftButton.setGraphic(runecraftButtonView);
    runecraftButton.setVisible(false);
    runecraftButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.RUNECRAFT);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(runecraftButton);

    constructionButton = new JFXButton();
    constructionButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(constructionButton, getTopAnchor(8));
    AnchorPane.setLeftAnchor(constructionButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(constructionButton, getRightAnchor(1));
    Image constructionButtonImg = new Image("skill_icons_small/construction.png");
    ImageView constructionButtonView = new ImageView(constructionButtonImg);
    constructionButton.setGraphic(constructionButtonView);
    constructionButton.setVisible(false);
    constructionButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.CONSTRUCTION);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(constructionButton);

    hitpointsButton = new JFXButton();
    hitpointsButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(hitpointsButton, getTopAnchor(1));
    AnchorPane.setLeftAnchor(hitpointsButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(hitpointsButton, getRightAnchor(2));
    Image hitpointsButtonImg = new Image("skill_icons_small/hitpoints.png");
    ImageView hitpointsButtonView = new ImageView(hitpointsButtonImg);
    hitpointsButton.setGraphic(hitpointsButtonView);
    hitpointsButton.setVisible(false);
    hitpointsButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.HITPOINTS);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(hitpointsButton);

    agilityButton = new JFXButton();
    agilityButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(agilityButton, getTopAnchor(2));
    AnchorPane.setLeftAnchor(agilityButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(agilityButton, getRightAnchor(2));
    Image agilityButtonImg = new Image("skill_icons_small/agility.png");
    ImageView agilityButtonView = new ImageView(agilityButtonImg);
    agilityButton.setGraphic(agilityButtonView);
    agilityButton.setVisible(false);
    agilityButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.AGILITY);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(agilityButton);

    herbloreButton = new JFXButton();
    herbloreButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(herbloreButton, getTopAnchor(3));
    AnchorPane.setLeftAnchor(herbloreButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(herbloreButton, getRightAnchor(2));
    Image herbloreButtonImg = new Image("skill_icons_small/herblore.png");
    ImageView herbloreButtonView = new ImageView(herbloreButtonImg);
    herbloreButton.setGraphic(herbloreButtonView);
    herbloreButton.setVisible(false);
    herbloreButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.HERBLORE);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(herbloreButton);

    thievingButton = new JFXButton();
    thievingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(thievingButton, getTopAnchor(4));
    AnchorPane.setLeftAnchor(thievingButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(thievingButton, getRightAnchor(2));
    Image thievingButtonImg = new Image("skill_icons_small/thieving.png");
    ImageView thievingButtonView = new ImageView(thievingButtonImg);
    thievingButton.setGraphic(thievingButtonView);
    thievingButton.setVisible(false);
    thievingButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.THIEVING);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(thievingButton);

    craftingButton = new JFXButton();
    craftingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(craftingButton, getTopAnchor(5));
    AnchorPane.setLeftAnchor(craftingButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(craftingButton, getRightAnchor(2));
    Image craftingButtonImg = new Image("skill_icons_small/crafting.png");
    ImageView craftingButtonView = new ImageView(craftingButtonImg);
    craftingButton.setGraphic(craftingButtonView);
    craftingButton.setVisible(false);
    craftingButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.CRAFTING);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(craftingButton);

    fletchingButton = new JFXButton();
    fletchingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(fletchingButton, getTopAnchor(6));
    AnchorPane.setLeftAnchor(fletchingButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(fletchingButton, getRightAnchor(2));
    Image fletchingButtonImg = new Image("skill_icons_small/fletching.png");
    ImageView fletchingButtonView = new ImageView(fletchingButtonImg);
    fletchingButton.setGraphic(fletchingButtonView);
    fletchingButton.setVisible(false);
    fletchingButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.FLETCHING);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(fletchingButton);

    slayerButton = new JFXButton();
    slayerButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(slayerButton, getTopAnchor(7));
    AnchorPane.setLeftAnchor(slayerButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(slayerButton, getRightAnchor(2));
    Image slayerButtonImg = new Image("skill_icons_small/slayer.png");
    ImageView slayerButtonView = new ImageView(slayerButtonImg);
    slayerButton.setGraphic(slayerButtonView);
    slayerButton.setVisible(false);
    slayerButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.SLAYER);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(slayerButton);

    hunterButton = new JFXButton();
    hunterButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(hunterButton, getTopAnchor(8));
    AnchorPane.setLeftAnchor(hunterButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(hunterButton, getRightAnchor(2));
    Image hunterButtonImg = new Image("skill_icons_small/hunter.png");
    ImageView hunterButtonView = new ImageView(hunterButtonImg);
    hunterButton.setGraphic(hunterButtonView);
    hunterButton.setVisible(false);
    hunterButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.HUNTER);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(hunterButton);

    miningButton = new JFXButton();
    miningButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(miningButton, getTopAnchor(1));
    AnchorPane.setLeftAnchor(miningButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(miningButton, getRightAnchor(3));
    Image miningButtonImg = new Image("skill_icons_small/mining.png");
    ImageView miningButtonView = new ImageView(miningButtonImg);
    miningButton.setGraphic(miningButtonView);
    miningButton.setVisible(false);
    miningButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.MINING);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(miningButton);

    smithingButton = new JFXButton();
    smithingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(smithingButton, getTopAnchor(2));
    AnchorPane.setLeftAnchor(smithingButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(smithingButton, getRightAnchor(3));
    Image smithingButtonImg = new Image("skill_icons_small/smithing.png");
    ImageView smithingButtonView = new ImageView(smithingButtonImg);
    smithingButton.setGraphic(smithingButtonView);
    smithingButton.setVisible(false);
    smithingButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.SMITHING);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(smithingButton);

    fishingButton = new JFXButton();
    fishingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(fishingButton, getTopAnchor(3));
    AnchorPane.setLeftAnchor(fishingButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(fishingButton, getRightAnchor(3));
    Image fishingButtonImg = new Image("skill_icons_small/fishing.png");
    ImageView fishingButtonView = new ImageView(fishingButtonImg);
    fishingButton.setGraphic(fishingButtonView);
    fishingButton.setVisible(false);
    fishingButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.FISHING);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(fishingButton);

    cookingButton = new JFXButton();
    cookingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(cookingButton, getTopAnchor(4));
    AnchorPane.setLeftAnchor(cookingButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(cookingButton, getRightAnchor(3));
    Image cookingButtonImg = new Image("skill_icons_small/cooking.png");
    ImageView cookingButtonView = new ImageView(cookingButtonImg);
    cookingButton.setGraphic(cookingButtonView);
    cookingButton.setVisible(false);
    cookingButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.COOKING);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(cookingButton);

    firemakingButton = new JFXButton();
    firemakingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(firemakingButton, getTopAnchor(5));
    AnchorPane.setLeftAnchor(firemakingButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(firemakingButton, getRightAnchor(3));
    Image firemakingButtonImg = new Image("skill_icons_small/firemaking.png");
    ImageView firemakingButtonView = new ImageView(firemakingButtonImg);
    firemakingButton.setGraphic(firemakingButtonView);
    firemakingButton.setVisible(false);
    firemakingButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.FIREMAKING);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(firemakingButton);

    woodcuttingButton = new JFXButton();
    woodcuttingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(woodcuttingButton, getTopAnchor(6));
    AnchorPane.setLeftAnchor(woodcuttingButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(woodcuttingButton, getRightAnchor(3));
    Image woodcuttingButtonImg = new Image("skill_icons_small/woodcutting.png");
    ImageView woodcuttingButtonView = new ImageView(woodcuttingButtonImg);
    woodcuttingButton.setGraphic(woodcuttingButtonView);
    woodcuttingButton.setVisible(false);
    woodcuttingButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.WOODCUTTING);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(woodcuttingButton);

    farmingButton = new JFXButton();
    farmingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(farmingButton, getTopAnchor(7));
    AnchorPane.setLeftAnchor(farmingButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(farmingButton, getRightAnchor(3));
    Image farmingButtonImg = new Image("skill_icons_small/farming.png");
    ImageView farmingButtonView = new ImageView(farmingButtonImg);
    farmingButton.setGraphic(farmingButtonView);
    farmingButton.setVisible(false);
    farmingButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      SkillOverviewController.updateSkill(lastResult, Skill.FARMING);
      ui.updateRightPanel(skillOverviewPanel);
    });
    skillPanel.getChildren().add(farmingButton);

    overallButton = new JFXButton();
    overallButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(overallButton, getTopAnchor(8));
    AnchorPane.setLeftAnchor(overallButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(overallButton, getRightAnchor(3));
    Image overallButtonImg = new Image("skill_icons_small/overall.png");
    ImageView overallButtonView = new ImageView(overallButtonImg);
    overallButton.setGraphic(overallButtonView);
    overallButton.setVisible(false);
    skillPanel.getChildren().add(overallButton);

    leaguePointsButton = new JFXButton();
    leaguePointsButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(leaguePointsButton, getTopAnchor(10));
    AnchorPane.setLeftAnchor(leaguePointsButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(leaguePointsButton, getRightAnchor(1));
    Image leaguePointsButtonImg = new Image("skill_icons_small/league_points.png");
    ImageView leaguePointsButtonView = new ImageView(leaguePointsButtonImg);
    leaguePointsButton.setGraphic(leaguePointsButtonView);
    leaguePointsButton.setVisible(false);
    JFXTooltip leaguePointsTooltip = new JFXTooltip("League Points");
    leaguePointsButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      leaguePointsTooltip.showOnAnchors(leaguePointsButton, 55, -20);
    });
    leaguePointsButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      leaguePointsTooltip.hide();
    });
    skillPanel.getChildren().add(leaguePointsButton);

    bountyHunterHunterButton = new JFXButton();
    bountyHunterHunterButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(bountyHunterHunterButton, getTopAnchor(10));
    AnchorPane.setLeftAnchor(bountyHunterHunterButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(bountyHunterHunterButton, getRightAnchor(2));
    Image bountyHunterHunterButtonImg = new Image("skill_icons_small/bounty_hunter_hunter.png");
    ImageView bountyHunterHunterButtonView = new ImageView(bountyHunterHunterButtonImg);
    bountyHunterHunterButton.setGraphic(bountyHunterHunterButtonView);
    bountyHunterHunterButton.setVisible(false);
    JFXTooltip bountyHunterHunterTooltip = new JFXTooltip("Bounty Hunter (Hunter)");
    bountyHunterHunterButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      bountyHunterHunterTooltip.showOnAnchors(bountyHunterHunterButton, 55, -20);
    });
    bountyHunterHunterButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      bountyHunterHunterTooltip.hide();
    });
    skillPanel.getChildren().add(bountyHunterHunterButton);

    bountyHunterRogueButton = new JFXButton();
    bountyHunterRogueButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(bountyHunterRogueButton, getTopAnchor(10));
    AnchorPane.setLeftAnchor(bountyHunterRogueButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(bountyHunterRogueButton, getRightAnchor(3));
    Image bountyHunterRogueButtonImg = new Image("skill_icons_small/bounty_hunter_rogue.png");
    ImageView bountyHunterRogueButtonView = new ImageView(bountyHunterRogueButtonImg);
    bountyHunterRogueButton.setGraphic(bountyHunterRogueButtonView);
    bountyHunterRogueButton.setVisible(false);
    JFXTooltip bountyHunterRogueTooltip = new JFXTooltip("League Points");
    bountyHunterRogueButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      bountyHunterRogueTooltip.showOnAnchors(bountyHunterRogueButton, 55, -20);
    });
    bountyHunterRogueButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      bountyHunterRogueTooltip.hide();
    });
    skillPanel.getChildren().add(bountyHunterRogueButton);

    lastManStandingButton = new JFXButton();
    lastManStandingButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(lastManStandingButton, getTopAnchor(11));
    AnchorPane.setLeftAnchor(lastManStandingButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(lastManStandingButton, getRightAnchor(1));
    Image lastManStandingButtonImg = new Image("skill_icons_small/last_man_standing.png");
    ImageView lastManStandingButtonView = new ImageView(lastManStandingButtonImg);
    lastManStandingButton.setGraphic(lastManStandingButtonView);
    lastManStandingButton.setVisible(false);
    JFXTooltip lastManStandingTooltip = new JFXTooltip("Last Man Standing");
    lastManStandingButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      lastManStandingTooltip.showOnAnchors(lastManStandingButton, 55, -20);
    });
    lastManStandingButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      lastManStandingTooltip.hide();
    });
    skillPanel.getChildren().add(lastManStandingButton);

    soulWarsZealButton = new JFXButton();
    soulWarsZealButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(soulWarsZealButton, getTopAnchor(11));
    AnchorPane.setLeftAnchor(soulWarsZealButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(soulWarsZealButton, getRightAnchor(2));
    Image soulWarsZealButtonImg = new Image("skill_icons_small/soul_wars_zeal.png");
    ImageView soulWarsZealButtonView = new ImageView(soulWarsZealButtonImg);
    soulWarsZealButton.setGraphic(soulWarsZealButtonView);
    soulWarsZealButton.setVisible(false);
    JFXTooltip soulWarsZealTooltip = new JFXTooltip("Soul Wars Zeal");
    soulWarsZealButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      soulWarsZealTooltip.showOnAnchors(soulWarsZealButton, 55, -20);
    });
    soulWarsZealButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      soulWarsZealTooltip.hide();
    });
    skillPanel.getChildren().add(soulWarsZealButton);

    abyssalSireButton = new JFXButton();
    abyssalSireButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(abyssalSireButton, getTopAnchor(13));
    AnchorPane.setLeftAnchor(abyssalSireButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(abyssalSireButton, getRightAnchor(1));
    Image abyssalSireButtonImg = new Image("skill_icons_small/bosses/abyssal_sire.png");
    ImageView abyssalSireButtonView = new ImageView(abyssalSireButtonImg);
    abyssalSireButton.setGraphic(abyssalSireButtonView);
    abyssalSireButton.setVisible(false);
    JFXTooltip bossName = new JFXTooltip("Abyssal Sire");
    abyssalSireButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      bossName.showOnAnchors(abyssalSireButton, 55, -20);
    });

    abyssalSireButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      bossName.hide();
    });
    skillPanel.getChildren().add(abyssalSireButton);

    alchemicalHydraButton = new JFXButton();
    alchemicalHydraButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(alchemicalHydraButton, getTopAnchor(13));
    AnchorPane.setLeftAnchor(alchemicalHydraButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(alchemicalHydraButton, getRightAnchor(2));
    Image alchemicalHydraButtonImg = new Image("skill_icons_small/bosses/alchemical_hydra.png");
    ImageView alchemicalHydraButtonView = new ImageView(alchemicalHydraButtonImg);
    alchemicalHydraButton.setGraphic(alchemicalHydraButtonView);
    alchemicalHydraButton.setVisible(false);
    JFXTooltip alchemicalHydraTooltip = new JFXTooltip("Alchemical Hydra");
    alchemicalHydraButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      alchemicalHydraTooltip.showOnAnchors(alchemicalHydraButton, 55, -20);
    });

    alchemicalHydraButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      alchemicalHydraTooltip.hide();
    });
    skillPanel.getChildren().add(alchemicalHydraButton);

    barrowsChestsButton = new JFXButton();
    barrowsChestsButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(barrowsChestsButton, getTopAnchor(13));
    AnchorPane.setLeftAnchor(barrowsChestsButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(barrowsChestsButton, getRightAnchor(3));
    Image barrowsChestsButtonImg = new Image("skill_icons_small/bosses/barrows_chests.png");
    ImageView barrowsChestsButtonView = new ImageView(barrowsChestsButtonImg);
    barrowsChestsButton.setGraphic(barrowsChestsButtonView);
    barrowsChestsButton.setVisible(false);
    JFXTooltip barrowsChestsTooltip = new JFXTooltip("Alchemical Hydra");
    barrowsChestsButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      barrowsChestsTooltip.showOnAnchors(barrowsChestsButton, 55, -20);
    });

    barrowsChestsButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      barrowsChestsTooltip.hide();
    });
    skillPanel.getChildren().add(barrowsChestsButton);

    bryophytaButton = new JFXButton();
    bryophytaButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(bryophytaButton, getTopAnchor(14));
    AnchorPane.setLeftAnchor(bryophytaButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(bryophytaButton, getRightAnchor(1));
    Image bryophytaButtonImg = new Image("skill_icons_small/bosses/bryophyta.png");
    ImageView bryophytaButtonView = new ImageView(bryophytaButtonImg);
    bryophytaButton.setGraphic(bryophytaButtonView);
    bryophytaButton.setVisible(false);
    JFXTooltip bryophytaTooltip = new JFXTooltip("Bryophyta");
    bryophytaButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      bryophytaTooltip.showOnAnchors(bryophytaButton, 55, -20);
    });

    bryophytaButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      bryophytaTooltip.hide();
    });
    skillPanel.getChildren().add(bryophytaButton);

    callistoButton = new JFXButton();
    callistoButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(callistoButton, getTopAnchor(14));
    AnchorPane.setLeftAnchor(callistoButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(callistoButton, getRightAnchor(2));
    Image callistoButtonImg = new Image("skill_icons_small/bosses/callisto.png");
    ImageView callistoButtonView = new ImageView(callistoButtonImg);
    callistoButton.setGraphic(callistoButtonView);
    callistoButton.setVisible(false);
    JFXTooltip callistoTooltip = new JFXTooltip("Callisto");
    callistoButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      callistoTooltip.showOnAnchors(callistoButton, 55, -20);
    });

    callistoButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      callistoTooltip.hide();
    });
    skillPanel.getChildren().add(callistoButton);

    cerberusButton = new JFXButton();
    cerberusButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(cerberusButton, getTopAnchor(14));
    AnchorPane.setLeftAnchor(cerberusButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(cerberusButton, getRightAnchor(3));
    Image cerberusButtonImg = new Image("skill_icons_small/bosses/cerberus.png");
    ImageView cerberusButtonView = new ImageView(cerberusButtonImg);
    cerberusButton.setGraphic(cerberusButtonView);
    cerberusButton.setVisible(false);
    JFXTooltip cerberusTooltip = new JFXTooltip("Cerberus");
    cerberusButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      cerberusTooltip.showOnAnchors(cerberusButton, 55, -20);
    });

    cerberusButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      cerberusTooltip.hide();
    });
    skillPanel.getChildren().add(cerberusButton);

    coxButton = new JFXButton();
    coxButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(coxButton, getTopAnchor(15));
    AnchorPane.setLeftAnchor(coxButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(coxButton, getRightAnchor(1));
    Image coxButtonImg = new Image("skill_icons_small/bosses/chambers_of_xeric.png");
    ImageView coxButtonView = new ImageView(coxButtonImg);
    coxButton.setGraphic(coxButtonView);
    coxButton.setVisible(false);
    JFXTooltip coxTooltip = new JFXTooltip("Chambers of Xeric");
    coxButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      coxTooltip.showOnAnchors(coxButton, 55, -20);
    });

    coxButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      coxTooltip.hide();
    });
    skillPanel.getChildren().add(coxButton);

    coxChallengeButton = new JFXButton();
    coxChallengeButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(coxChallengeButton, getTopAnchor(15));
    AnchorPane.setLeftAnchor(coxChallengeButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(coxChallengeButton, getRightAnchor(2));
    Image coxChallengeButtonImg = new Image("skill_icons_small/bosses/chambers_of_xeric_challenge_mode.png");
    ImageView coxChallengeButtonView = new ImageView(coxChallengeButtonImg);
    coxChallengeButton.setGraphic(coxChallengeButtonView);
    coxChallengeButton.setVisible(false);
    JFXTooltip coxChallengeTooltip = new JFXTooltip("Chambers of Xeric (Challenge)");
    coxChallengeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      coxChallengeTooltip.showOnAnchors(coxChallengeButton, 55, -20);
    });

    coxChallengeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      coxChallengeTooltip.hide();
    });
    skillPanel.getChildren().add(coxChallengeButton);

    chaosElementalButton = new JFXButton();
    chaosElementalButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(chaosElementalButton, getTopAnchor(15));
    AnchorPane.setLeftAnchor(chaosElementalButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(chaosElementalButton, getRightAnchor(3));
    Image chaosElementalButtonImg = new Image("skill_icons_small/bosses/chaos_elemental.png");
    ImageView chaosElementalButtonView = new ImageView(chaosElementalButtonImg);
    chaosElementalButton.setGraphic(chaosElementalButtonView);
    chaosElementalButton.setVisible(false);
    JFXTooltip chaosElementalTooltip = new JFXTooltip("Chaos Elemental");
    chaosElementalButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      chaosElementalTooltip.showOnAnchors(chaosElementalButton, 55, -20);
    });

    chaosElementalButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      chaosElementalTooltip.hide();
    });
    skillPanel.getChildren().add(chaosElementalButton);

    chaosFanaticButton = new JFXButton();
    chaosFanaticButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(chaosFanaticButton, getTopAnchor(16));
    AnchorPane.setLeftAnchor(chaosFanaticButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(chaosFanaticButton, getRightAnchor(1));
    Image chaosFanaticButtonImg = new Image("skill_icons_small/bosses/chaos_fanatic.png");
    ImageView chaosFanaticButtonView = new ImageView(chaosFanaticButtonImg);
    chaosFanaticButton.setGraphic(chaosFanaticButtonView);
    chaosFanaticButton.setVisible(false);
    JFXTooltip chaosFanaticTooltip = new JFXTooltip("Chaos Fanatic");
    chaosFanaticButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      chaosFanaticTooltip.showOnAnchors(chaosFanaticButton, 55, -20);
    });

    chaosFanaticButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      chaosFanaticTooltip.hide();
    });
    skillPanel.getChildren().add(chaosFanaticButton);

    commanderZilyanaButton = new JFXButton();
    commanderZilyanaButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(commanderZilyanaButton, getTopAnchor(16));
    AnchorPane.setLeftAnchor(commanderZilyanaButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(commanderZilyanaButton, getRightAnchor(2));
    Image commanderZilyanaButtonImg = new Image("skill_icons_small/bosses/commander_zilyana.png");
    ImageView commanderZilyanaButtonView = new ImageView(commanderZilyanaButtonImg);
    commanderZilyanaButton.setGraphic(commanderZilyanaButtonView);
    commanderZilyanaButton.setVisible(false);
    JFXTooltip commanderZilyanaTooltip = new JFXTooltip("Commander Zilyana");
    commanderZilyanaButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      commanderZilyanaTooltip.showOnAnchors(commanderZilyanaButton, 55, -20);
    });

    commanderZilyanaButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      commanderZilyanaTooltip.hide();
    });
    skillPanel.getChildren().add(commanderZilyanaButton);

    corpButton = new JFXButton();
    corpButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(corpButton, getTopAnchor(16));
    AnchorPane.setLeftAnchor(corpButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(corpButton, getRightAnchor(3));
    Image corpButtonImg = new Image("skill_icons_small/bosses/corporeal_beast.png");
    ImageView corpButtonView = new ImageView(corpButtonImg);
    corpButton.setGraphic(corpButtonView);
    corpButton.setVisible(false);
    JFXTooltip corpTooltip = new JFXTooltip("Corporeal Beast");
    corpButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      corpTooltip.showOnAnchors(corpButton, 55, -20);
    });

    corpButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      corpTooltip.hide();
    });
    skillPanel.getChildren().add(corpButton);

    crazyArchButton = new JFXButton();
    crazyArchButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(crazyArchButton, getTopAnchor(17));
    AnchorPane.setLeftAnchor(crazyArchButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(crazyArchButton, getRightAnchor(1));
    Image crazyArchButtonImg = new Image("skill_icons_small/bosses/crazy_archaeologist.png");
    ImageView crazyArchButtonView = new ImageView(crazyArchButtonImg);
    crazyArchButton.setGraphic(crazyArchButtonView);
    crazyArchButton.setVisible(false);
    JFXTooltip crazyArchTooltip = new JFXTooltip("Crazy Archaeologist");
    crazyArchButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      crazyArchTooltip.showOnAnchors(crazyArchButton, 55, -20);
    });

    crazyArchButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      crazyArchTooltip.hide();
    });
    skillPanel.getChildren().add(crazyArchButton);

    dagannothPrimeButton = new JFXButton();
    dagannothPrimeButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(dagannothPrimeButton, getTopAnchor(17));
    AnchorPane.setLeftAnchor(dagannothPrimeButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(dagannothPrimeButton, getRightAnchor(2));
    Image dagannothPrimeButtonImg = new Image("skill_icons_small/bosses/dagannoth_prime.png");
    ImageView dagannothPrimeButtonView = new ImageView(dagannothPrimeButtonImg);
    dagannothPrimeButton.setGraphic(dagannothPrimeButtonView);
    dagannothPrimeButton.setVisible(false);
    JFXTooltip dagannothPrimeTooltip = new JFXTooltip("Dagannoth Prime");
    dagannothPrimeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      dagannothPrimeTooltip.showOnAnchors(dagannothPrimeButton, 55, -20);
    });

    dagannothPrimeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      dagannothPrimeTooltip.hide();
    });
    skillPanel.getChildren().add(dagannothPrimeButton);

    dagannothRexButton = new JFXButton();
    dagannothRexButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(dagannothRexButton, getTopAnchor(17));
    AnchorPane.setLeftAnchor(dagannothRexButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(dagannothRexButton, getRightAnchor(3));
    Image dagannothRexButtonImg = new Image("skill_icons_small/bosses/dagannoth_rex.png");
    ImageView dagannothRexButtonView = new ImageView(dagannothRexButtonImg);
    dagannothRexButton.setGraphic(dagannothRexButtonView);
    dagannothRexButton.setVisible(false);
    JFXTooltip dagannothRexTooltip = new JFXTooltip("Dagannoth Rex");
    dagannothRexButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      dagannothRexTooltip.showOnAnchors(dagannothRexButton, 55, -20);
    });

    dagannothRexButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      dagannothRexTooltip.hide();
    });
    skillPanel.getChildren().add(dagannothRexButton);

    dagannothSupremeButton = new JFXButton();
    dagannothSupremeButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(dagannothSupremeButton, getTopAnchor(18));
    AnchorPane.setLeftAnchor(dagannothSupremeButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(dagannothSupremeButton, getRightAnchor(1));
    Image dagannothSupremeButtonImg = new Image("skill_icons_small/bosses/dagannoth_supreme.png");
    ImageView dagannothSupremeButtonView = new ImageView(dagannothSupremeButtonImg);
    dagannothSupremeButton.setGraphic(dagannothSupremeButtonView);
    dagannothSupremeButton.setVisible(false);
    JFXTooltip dagannothSupremeTooltip = new JFXTooltip("Dagannoth Supreme");
    dagannothSupremeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      dagannothSupremeTooltip.showOnAnchors(dagannothSupremeButton, 55, -20);
    });

    dagannothSupremeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      dagannothSupremeTooltip.hide();
    });
    skillPanel.getChildren().add(dagannothSupremeButton);

    derangedArchButton = new JFXButton();
    derangedArchButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(derangedArchButton, getTopAnchor(18));
    AnchorPane.setLeftAnchor(derangedArchButton, getLeftAnchor(2));
    AnchorPane.setRightAnchor(derangedArchButton, getRightAnchor(2));
    Image derangedArchButtonImg = new Image("skill_icons_small/bosses/deranged_archaeologist.png");
    ImageView derangedArchButtonView = new ImageView(derangedArchButtonImg);
    derangedArchButton.setGraphic(derangedArchButtonView);
    derangedArchButton.setVisible(false);
    JFXTooltip derangedArchTooltip = new JFXTooltip("Deranged Archaeologist");
    derangedArchButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      derangedArchTooltip.showOnAnchors(derangedArchButton, 55, -20);
    });

    derangedArchButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      derangedArchTooltip.hide();
    });
    skillPanel.getChildren().add(derangedArchButton);

    generalGraardorButton = new JFXButton();
    generalGraardorButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(generalGraardorButton, getTopAnchor(18));
    AnchorPane.setLeftAnchor(generalGraardorButton, getLeftAnchor(3));
    AnchorPane.setRightAnchor(generalGraardorButton, getRightAnchor(3));
    Image generalGraardorButtonImg = new Image("skill_icons_small/bosses/general_graardor.png");
    ImageView generalGraardorButtonView = new ImageView(generalGraardorButtonImg);
    generalGraardorButton.setGraphic(generalGraardorButtonView);
    generalGraardorButton.setVisible(false);
    JFXTooltip generalGraardorTooltip = new JFXTooltip("General Graardor");
    generalGraardorButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      generalGraardorTooltip.showOnAnchors(generalGraardorButton, 55, -20);
    });

    generalGraardorButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      generalGraardorTooltip.hide();
    });
    skillPanel.getChildren().add(generalGraardorButton);

    giantMoleButton = new JFXButton();
    giantMoleButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(giantMoleButton, getTopAnchor(19));
    AnchorPane.setLeftAnchor(giantMoleButton, getLeftAnchor(1));
    AnchorPane.setRightAnchor(giantMoleButton, getRightAnchor(1));
    Image giantMoleButtonImg = new Image("skill_icons_small/bosses/giant_mole.png");
    ImageView giantMoleButtonView = new ImageView(giantMoleButtonImg);
    giantMoleButton.setGraphic(giantMoleButtonView);
    giantMoleButton.setVisible(false);
    JFXTooltip giantMoleTooltip = new JFXTooltip("Giant Mole");
    giantMoleButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      giantMoleTooltip.showOnAnchors(giantMoleButton, 55, -20);
    });

    giantMoleButton.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      giantMoleTooltip.hide();
    });
    skillPanel.getChildren().add(giantMoleButton);

    grotesqueGuardiansButton = new KillScoreButton("skill_icons_small/bosses/grotesque_guardians.png", 19, 2);
    skillPanel.getChildren().add(grotesqueGuardiansButton);
    hesporiButton = new KillScoreButton("skill_icons_small/bosses/hespori.png", 19, 3);
    skillPanel.getChildren().add(hesporiButton);
    kalphiteQueenButton = new KillScoreButton("skill_icons_small/bosses/kalphite_queen.png", 20, 1);
    skillPanel.getChildren().add(kalphiteQueenButton);
    kbdButton = new KillScoreButton("skill_icons_small/bosses/king_black_dragon.png", 20, 2);
    skillPanel.getChildren().add(kbdButton);
    krakenButton = new KillScoreButton("skill_icons_small/bosses/kraken.png", 20, 3);
    skillPanel.getChildren().add(krakenButton);
    kreearraButton = new KillScoreButton("skill_icons_small/bosses/kreearra.png", 21, 1);
    skillPanel.getChildren().add(kreearraButton);
    krilTsutsarothButton = new KillScoreButton("skill_icons_small/bosses/kril_tsutsaroth.png", 21, 2);
    skillPanel.getChildren().add(krilTsutsarothButton);
    mimicButton = new KillScoreButton("skill_icons_small/bosses/mimic.png", 21, 3);
    skillPanel.getChildren().add(mimicButton);
    nightmareButton = new KillScoreButton("skill_icons_small/bosses/nightmare.png", 22, 1);
    skillPanel.getChildren().add(nightmareButton);
    phosanisNightmareButton = new KillScoreButton("skill_icons_small/bosses/nightmare.png", 22, 2);
    skillPanel.getChildren().add(phosanisNightmareButton);
    oborButton = new KillScoreButton("skill_icons_small/bosses/obor.png", 22, 3);
    skillPanel.getChildren().add(oborButton);
    sarachnisButton = new KillScoreButton("skill_icons_small/bosses/sarachnis.png", 23, 1);
    skillPanel.getChildren().add(sarachnisButton);
    scorpiaButton = new KillScoreButton("skill_icons_small/bosses/scorpia.png", 23, 2);
    skillPanel.getChildren().add(scorpiaButton);
    skotizoButton = new KillScoreButton("skill_icons_small/bosses/skotizo.png", 23, 3);
    skillPanel.getChildren().add(skotizoButton);
    temporossButton = new KillScoreButton("skill_icons_small/bosses/tempoross.png", 24, 1);
    skillPanel.getChildren().add(temporossButton);
    theGauntletButton = new KillScoreButton("skill_icons_small/bosses/the_gauntlet.png", 24, 2);
    skillPanel.getChildren().add(theGauntletButton);
    theCorruptedGauntletButton = new KillScoreButton("skill_icons_small/bosses/the_corrupted_gauntlet.png", 24, 3);
    skillPanel.getChildren().add(theCorruptedGauntletButton);
    tobButton = new KillScoreButton("skill_icons_small/bosses/theatre_of_blood.png", 25, 1);
    skillPanel.getChildren().add(tobButton);
    tobHardButton = new KillScoreButton("skill_icons_small/bosses/theatre_of_blood_hard_mode.png", 25, 2);
    skillPanel.getChildren().add(tobHardButton);
    thermieButton = new KillScoreButton("skill_icons_small/bosses/thermonuclear_smoke_devil.png", 25, 3);
    skillPanel.getChildren().add(thermieButton);
    zukButton = new KillScoreButton("skill_icons_small/bosses/tzkal_zuk.png", 26, 1);
    skillPanel.getChildren().add(zukButton);
    jadButton = new KillScoreButton("skill_icons_small/bosses/tztok_jad.png", 26, 2);
    skillPanel.getChildren().add(jadButton);
    venenatisButton = new KillScoreButton("skill_icons_small/bosses/venenatis.png", 26, 3);
    skillPanel.getChildren().add(venenatisButton);
    vetionButton = new KillScoreButton("skill_icons_small/bosses/vetion.png", 27, 1);
    skillPanel.getChildren().add(vetionButton);
    vorkathButton = new KillScoreButton("skill_icons_small/bosses/vorkath.png", 27, 2);
    skillPanel.getChildren().add(vorkathButton);
    wintertodtButton = new KillScoreButton("skill_icons_small/bosses/wintertodt.png", 27, 3);
    skillPanel.getChildren().add(wintertodtButton);
    zalcanoButton = new KillScoreButton("skill_icons_small/bosses/zalcano.png", 28, 1);
    skillPanel.getChildren().add(zalcanoButton);
    zulrahButton = new KillScoreButton("skill_icons_small/bosses/zulrah.png", 28, 2);
    skillPanel.getChildren().add(zulrahButton);
  }

  private double getTopAnchor(int row) {
    return 210.0 + ((row - 1) * 35);
  }

  private double getLeftAnchor(int column) {
    return switch (column) {
      case 3 -> 240;
      case 2 -> 135;
      case 1 -> 14;
      default -> -1;
    };
  }

  private double getRightAnchor(int column) {
    return switch (column) {
      case 3 -> 18;
      case 2 -> 120;
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
    Logger.getLogger(getClass()).error("lookup");
    resetButtons();
    final String lookup = sanitize(searchBox.getText());

    if (lastLookup.equals(lookup)) {
      updateWithResult(lastResult);
      return;
    }
    configManager.setConfiguration("wiseTracker", "lastSearch", lookup);
    lastLookup = lookup;

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
    SkillOverviewController.user = lookup;
    searchBox.setEditable(false);
    loading = true;


    Platform.runLater(() -> {
      BasicResponse result = wiseOldManClient.lookup(lookup);
      if (result == null)
        return;
      lastResult = result;
      //successful player search
      searchBox.setEditable(true);
      loading = false;
      updateWithResult(result);
    });
  }

  public void createSkillTooltip(JFXButton button, SkillSnapshot snapshot) {
    JFXTooltip tooltip = new JFXTooltip(snapshot.skill.getName() + "\n"
        + "Rank: " + formatter.format(snapshot.rank) + "\n"
        + "Experience: " + formatter.format(snapshot.xp) + "\n"
        + "EHP: " + formatter.format(snapshot.ehp));
    tooltip.setFont(Font.font(14));

    button.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      tooltip.showOnAnchors(button, 55, -90);
    });
    button.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      tooltip.hide();
    });
  }

  public void createKillTooltip(JFXButton button, String name, KillSnapshot snapshot) {
    JFXTooltip tooltip = new JFXTooltip(name + "\n"
        + "Rank: " + formatter.format(snapshot.rank) + "\n"
        + "Kills: " + formatter.format(snapshot.kills) + "\n"
        + "EHB: " + formatter.format(snapshot.ehb));
    tooltip.setFont(Font.font(14));

    button.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      tooltip.showOnAnchors(button, 55, -90);
    });
    button.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      tooltip.hide();
    });
  }

  public void updateWithResult(BasicResponse result) {
    displayNameLabel.setText("Display Name: " + result.displayName);
    displayNameLabel.setVisible(true);
    typeLabel.setText("Type: " + result.type);
    typeLabel.setVisible(true);
    buildLabel.setText("Build: " + result.build);
    buildLabel.setVisible(true);
    combatLvlLabel.setText("Combat LVL: " + result.combatLvl);
    combatLvlLabel.setVisible(true);
    ehpLabel.setText("Efficient Hours Played: " + formatter.format(result.ehp));
    ehpLabel.setVisible(true);
    ehbLabel.setText("Efficient Hours Bossed: " + formatter.format(result.ehb));
    ehbLabel.setVisible(true);
    ttmLabel.setText("Hours To Max: " + formatter.format(result.ttm));
    ttmLabel.setVisible(true);
    tt200mLabel.setText("Hours To all 200m: " + formatter.format(result.tt200m));
    tt200mLabel.setVisible(true);
    attackButton.setText("  " + result.latestSnapshot.attack.lvl);
    attackButton.setVisible(true);
    createSkillTooltip(attackButton, result.latestSnapshot.attack);
    strengthButton.setText("  " + result.latestSnapshot.strength.lvl);
    strengthButton.setVisible(true);
    createSkillTooltip(strengthButton, result.latestSnapshot.strength);
    defenceButton.setText("  " + result.latestSnapshot.defence.lvl);
    defenceButton.setVisible(true);
    createSkillTooltip(defenceButton, result.latestSnapshot.defence);
    rangedButton.setText("  " + result.latestSnapshot.ranged.lvl);
    rangedButton.setVisible(true);
    createSkillTooltip(rangedButton, result.latestSnapshot.ranged);
    prayerButton.setText("  " + result.latestSnapshot.prayer.lvl);
    prayerButton.setVisible(true);
    createSkillTooltip(prayerButton, result.latestSnapshot.prayer);
    magicButton.setText("  " + result.latestSnapshot.magic.lvl);
    magicButton.setVisible(true);
    createSkillTooltip(magicButton, result.latestSnapshot.magic);
    runecraftButton.setText("  " + result.latestSnapshot.runecrafting.lvl);
    runecraftButton.setVisible(true);
    createSkillTooltip(runecraftButton, result.latestSnapshot.runecrafting);
    constructionButton.setText("  " + result.latestSnapshot.construction.lvl);
    constructionButton.setVisible(true);
    createSkillTooltip(constructionButton, result.latestSnapshot.construction);
    hitpointsButton.setText("  " + result.latestSnapshot.hitpoints.lvl);
    hitpointsButton.setVisible(true);
    createSkillTooltip(hitpointsButton, result.latestSnapshot.hitpoints);
    miningButton.setText("  " + result.latestSnapshot.mining.lvl);
    miningButton.setVisible(true);
    createSkillTooltip(miningButton, result.latestSnapshot.mining);
    agilityButton.setText("  " + result.latestSnapshot.agility.lvl);
    agilityButton.setVisible(true);
    createSkillTooltip(agilityButton, result.latestSnapshot.agility);
    herbloreButton.setText("  " + result.latestSnapshot.herblore.lvl);
    herbloreButton.setVisible(true);
    createSkillTooltip(herbloreButton, result.latestSnapshot.herblore);
    thievingButton.setText("  " + result.latestSnapshot.thieving.lvl);
    thievingButton.setVisible(true);
    createSkillTooltip(thievingButton, result.latestSnapshot.thieving);
    craftingButton.setText("  " + result.latestSnapshot.crafting.lvl);
    craftingButton.setVisible(true);
    createSkillTooltip(craftingButton, result.latestSnapshot.crafting);
    fletchingButton.setText("  " + result.latestSnapshot.fletching.lvl);
    fletchingButton.setVisible(true);
    createSkillTooltip(fletchingButton, result.latestSnapshot.fletching);
    slayerButton.setText("  " + result.latestSnapshot.slayer.lvl);
    slayerButton.setVisible(true);
    createSkillTooltip(slayerButton, result.latestSnapshot.slayer);
    hunterButton.setText("  " + result.latestSnapshot.hunter.lvl);
    hunterButton.setVisible(true);
    createSkillTooltip(hunterButton, result.latestSnapshot.hunter);
    smithingButton.setText("  " + result.latestSnapshot.smithing.lvl);
    smithingButton.setVisible(true);
    createSkillTooltip(smithingButton, result.latestSnapshot.smithing);
    fishingButton.setText("  " + result.latestSnapshot.fishing.lvl);
    fishingButton.setVisible(true);
    createSkillTooltip(fishingButton, result.latestSnapshot.fishing);
    cookingButton.setText("  " + result.latestSnapshot.cooking.lvl);
    cookingButton.setVisible(true);
    createSkillTooltip(cookingButton, result.latestSnapshot.cooking);
    firemakingButton.setText("  " + result.latestSnapshot.firemaking.lvl);
    firemakingButton.setVisible(true);
    createSkillTooltip(firemakingButton, result.latestSnapshot.firemaking);
    woodcuttingButton.setText("  " + result.latestSnapshot.woodcutting.lvl);
    woodcuttingButton.setVisible(true);
    createSkillTooltip(woodcuttingButton, result.latestSnapshot.woodcutting);
    farmingButton.setText("  " + result.latestSnapshot.farming.lvl);
    farmingButton.setVisible(true);
    createSkillTooltip(farmingButton, result.latestSnapshot.farming);
    overallButton.setText("  " + result.latestSnapshot.overall.lvl);
    overallButton.setVisible(true);
    createSkillTooltip(overallButton, result.latestSnapshot.overall);

    leaguePointsButton.setText("  " + result.latestSnapshot.leaguePoints.score);
    leaguePointsButton.setVisible(true);
    bountyHunterHunterButton.setText("  " + result.latestSnapshot.bountyHunterHunter.score);
    bountyHunterHunterButton.setVisible(true);
    bountyHunterRogueButton.setText("  " + result.latestSnapshot.bountyHunterRogue.score);
    bountyHunterRogueButton.setVisible(true);
    lastManStandingButton.setText("  " + result.latestSnapshot.lastManStanding.score);
    lastManStandingButton.setVisible(true);
    soulWarsZealButton.setText("  " + result.latestSnapshot.soulWarsZeal.score);
    soulWarsZealButton.setVisible(true);

    abyssalSireButton.setText("  " + result.latestSnapshot.abyssalSire.kills);
    abyssalSireButton.setVisible(true);
    alchemicalHydraButton.setText("  " + result.latestSnapshot.alchemicalHydra.kills);
    alchemicalHydraButton.setVisible(true);
    barrowsChestsButton.setText("  " + result.latestSnapshot.barrowsChests.kills);
    barrowsChestsButton.setVisible(true);
    bryophytaButton.setText("  " + result.latestSnapshot.bryophyta.kills);
    bryophytaButton.setVisible(true);
    callistoButton.setText("  " + result.latestSnapshot.callisto.kills);
    callistoButton.setVisible(true);
    cerberusButton.setText("  " + result.latestSnapshot.cerberus.kills);
    cerberusButton.setVisible(true);
    coxButton.setText("  " + result.latestSnapshot.cox.kills);
    coxButton.setVisible(true);
    coxChallengeButton.setText("  " + result.latestSnapshot.coxChallenge.kills);
    coxChallengeButton.setVisible(true);
    chaosElementalButton.setText("  " + result.latestSnapshot.chaosElemental.kills);
    chaosElementalButton.setVisible(true);
    chaosFanaticButton.setText("  " + result.latestSnapshot.chaosFanatic.kills);
    chaosFanaticButton.setVisible(true);
    commanderZilyanaButton.setText("  " + result.latestSnapshot.commanderZilyana.kills);
    commanderZilyanaButton.setVisible(true);
    corpButton.setText("  " + result.latestSnapshot.corp.kills);
    corpButton.setVisible(true);
    crazyArchButton.setText("  " + result.latestSnapshot.crazyArch.kills);
    crazyArchButton.setVisible(true);
    dagannothPrimeButton.setText("  " + result.latestSnapshot.dagannothPrime.kills);
    dagannothPrimeButton.setVisible(true);
    dagannothRexButton.setText("  " + result.latestSnapshot.dagannothRex.kills);
    dagannothRexButton.setVisible(true);
    dagannothSupremeButton.setText("  " + result.latestSnapshot.dagannothSupreme.kills);
    dagannothSupremeButton.setVisible(true);
    derangedArchButton.setText("  " + result.latestSnapshot.derangedArch.kills);
    derangedArchButton.setVisible(true);
    generalGraardorButton.setText("  " + result.latestSnapshot.generalGraardor.kills);
    generalGraardorButton.setVisible(true);
    giantMoleButton.setText("  " + result.latestSnapshot.giantMole.kills);
    giantMoleButton.setVisible(true);
    grotesqueGuardiansButton.setText("  " + result.latestSnapshot.grotesqueGuardians.kills);
    grotesqueGuardiansButton.setVisible(true);
    hesporiButton.setText("  " + result.latestSnapshot.hespori.kills);
    hesporiButton.setVisible(true);
    kalphiteQueenButton.setText("  " + result.latestSnapshot.kalphiteQueen.kills);
    kalphiteQueenButton.setVisible(true);
    kbdButton.setText("  " + result.latestSnapshot.kbd.kills);
    kbdButton.setVisible(true);
    krakenButton.setText("  " + result.latestSnapshot.kraken.kills);
    krakenButton.setVisible(true);
    kreearraButton.setText("  " + result.latestSnapshot.kreearra.kills);
    kreearraButton.setVisible(true);
    krilTsutsarothButton.setText("  " + result.latestSnapshot.krilTsutsaroth.kills);
    krilTsutsarothButton.setVisible(true);
    mimicButton.setText("  " + result.latestSnapshot.mimic.kills);
    mimicButton.setVisible(true);
    nightmareButton.setText("  " + result.latestSnapshot.nightmare.kills);
    nightmareButton.setVisible(true);
    phosanisNightmareButton.setText("  " + result.latestSnapshot.phosanisNightmare.kills);
    phosanisNightmareButton.setVisible(true);
    oborButton.setText("  " + result.latestSnapshot.obor.kills);
    oborButton.setVisible(true);
    sarachnisButton.setText("  " + result.latestSnapshot.sarachnis.kills);
    sarachnisButton.setVisible(true);
    scorpiaButton.setText("  " + result.latestSnapshot.scorpia.kills);
    scorpiaButton.setVisible(true);
    skotizoButton.setText("  " + result.latestSnapshot.skotizo.kills);
    skotizoButton.setVisible(true);
    temporossButton.setText("  " + result.latestSnapshot.tempoross.kills);
    temporossButton.setVisible(true);
    theGauntletButton.setText("  " + result.latestSnapshot.theGauntlet.kills);
    theGauntletButton.setVisible(true);
    theCorruptedGauntletButton.setText("  " + result.latestSnapshot.theCorruptedGauntlet.kills);
    theCorruptedGauntletButton.setVisible(true);
    tobButton.setText("  " + result.latestSnapshot.tob.kills);
    tobButton.setVisible(true);
    tobHardButton.setText("  " + result.latestSnapshot.tobHard.kills);
    tobHardButton.setVisible(true);
    thermieButton.setText("  " + result.latestSnapshot.thermie.kills);
    thermieButton.setVisible(true);
    zukButton.setText("  " + result.latestSnapshot.zuk.kills);
    zukButton.setVisible(true);
    jadButton.setText("  " + result.latestSnapshot.jad.kills);
    jadButton.setVisible(true);
    venenatisButton.setText("  " + result.latestSnapshot.venenatis.kills);
    venenatisButton.setVisible(true);
    vetionButton.setText("  " + result.latestSnapshot.vetion.kills);
    vetionButton.setVisible(true);
    vorkathButton.setText("  " + result.latestSnapshot.vorkath.kills);
    vorkathButton.setVisible(true);
    wintertodtButton.setText("  " + result.latestSnapshot.wintertodt.kills);
    wintertodtButton.setVisible(true);
    zalcanoButton.setText("  " + result.latestSnapshot.zalcano.kills);
    zalcanoButton.setVisible(true);
    zulrahButton.setText("  " + result.latestSnapshot.zulrah.kills);
    zulrahButton.setVisible(true);
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
