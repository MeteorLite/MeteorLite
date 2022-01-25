package meteor.plugins.hiscorewise;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import meteor.MeteorLiteClientLauncher;
import meteor.util.MeteorConstants;
import net.runelite.api.Skill;
import net.runelite.http.api.wiseoldman.Period;
import net.runelite.http.api.wiseoldman.WiseOldManClient;
import net.runelite.http.api.wiseoldman.model.BasicResponse;
import net.runelite.http.api.wiseoldman.model.SkillSnapshot;
import net.runelite.http.api.wiseoldman.model.Snapshot;
import org.sponge.util.Logger;

public class SkillOverviewController {

  public static ScrollPane staticAP;
  public static SkillOverviewController INSTANCE;
  public static String user;
  private static Text levelLabel;
  private static ArrayList<Snapshot> cachedFiveMin;
  private static ArrayList<Snapshot> cachedDay;
  private static ArrayList<Snapshot> cachedWeek;
  private static ArrayList<Snapshot> cachedMonth;
  private static ArrayList<Snapshot> cachedYear;
  private static String lastUser = "";
  private static Text rankLabel;
  private static Text xpLabel;
  private static Text ehpLabel;

  @FXML
  ScrollPane hiscorePanel;

  private static AnchorPane skillPanel = new AnchorPane();
  private static VBox scrollPanel = new VBox();

  private static WiseOldManClient wiseOldManClient;

  private static Skill currentSkill;

  private JFXButton backButton = new JFXButton("Back");

  private static Logger log = Logger.getLogger(SkillOverviewController.class);
  private static Text skillNameText;
  private static Text snapshotsText;

  static DecimalFormat formatter = new DecimalFormat("#,###");

  @FXML
  protected void initialize() {
    MeteorLiteClientLauncher.injector.injectMembers(this);
    staticAP = hiscorePanel;
    wiseOldManClient = new WiseOldManClient();
    INSTANCE = this;
    hiscorePanel.getStylesheets().add("css/plugins/jfx-scrollbar.css");
    scrollPanel.setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));

    backButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    FontAwesomeIconView graphic = new FontAwesomeIconView(FontAwesomeIcon.ARROW_LEFT);
    graphic.setFill(Paint.valueOf("CYAN"));
    backButton.setGraphic(graphic);
    backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> HiscoreWisePlugin.showPanel());
    AnchorPane.setLeftAnchor(backButton, 8.0);
    AnchorPane.setTopAnchor(backButton, 8.0);

    skillNameText = new Text();
    skillNameText.setFill(Paint.valueOf("CYAN"));
    AnchorPane.setLeftAnchor(skillNameText, 140.0);
    AnchorPane.setTopAnchor(skillNameText, 8.0);
    skillNameText.setFont(Font.font(20.0));


    snapshotsText = new Text();
    snapshotsText.setFill(Paint.valueOf("CYAN"));
    AnchorPane.setLeftAnchor(snapshotsText, 140.0);
    AnchorPane.setTopAnchor(snapshotsText, 32.0);

    rankLabel = new Text();
    levelLabel = new Text();
    xpLabel = new Text();
    ehpLabel = new Text();

    hiscorePanel.setFitToWidth(true);
  }

  public static void updateSkill(BasicResponse lastResult,
      Skill skill) {
    skillPanel.getChildren().clear();
    skillPanel.getChildren().add(INSTANCE.backButton);
    skillPanel.getChildren().add(skillNameText);
    skillPanel.getChildren().add(snapshotsText);
    scrollPanel.getChildren().clear();
    scrollPanel.getChildren().add(skillPanel);
    INSTANCE.hiscorePanel.setContent(scrollPanel);

    SkillSnapshot skillSnapshot = lastResult.latestSnapshot.getSnapshotFromSkill(skill);

    rankLabel.setFill(Paint.valueOf("CYAN"));
    rankLabel.setLayoutX(5);
    rankLabel.setLayoutY(85);

    rankLabel.setText("Rank: " + formatter.format(skillSnapshot.rank));
    scrollPanel.getChildren().add(rankLabel);

    levelLabel.setFill(Paint.valueOf("CYAN"));
    levelLabel.setLayoutX(5);
    levelLabel.setLayoutY(100);
    levelLabel.setText("Level: " + skillSnapshot.lvl);
    scrollPanel.getChildren().add(levelLabel);

    xpLabel.setFill(Paint.valueOf("CYAN"));
    xpLabel.setLayoutX(5);
    xpLabel.setLayoutY(115);
    xpLabel.setText("Experience: " + formatter.format(skillSnapshot.xp));
    scrollPanel.getChildren().add(xpLabel);

    ehpLabel.setFill(Paint.valueOf("CYAN"));
    ehpLabel.setLayoutX(5);
    ehpLabel.setLayoutY(130);
    ehpLabel.setText("EHP: " + formatter.format(skillSnapshot.ehp));
    scrollPanel.getChildren().add(ehpLabel);

    skillNameText.setText(skill.getName());
    currentSkill = skill;

    ArrayList<Snapshot> fiveMinSnapshots;
    ArrayList<Snapshot> daySnapshots;
    ArrayList<Snapshot> weekSnapshots;
    ArrayList<Snapshot> monthSnapshots;
    ArrayList<Snapshot> yearSnapshots;

    if (lastUser.equals(user)) {
      fiveMinSnapshots = cachedFiveMin;
      daySnapshots = cachedDay;
      weekSnapshots = cachedWeek;
      monthSnapshots = cachedMonth;
      yearSnapshots = cachedYear;
    }
    else {
      fiveMinSnapshots = wiseOldManClient.lookupSnapshots(user, Period.FIVE_MIN);
      daySnapshots = wiseOldManClient.lookupSnapshots(user, Period.DAY);
      weekSnapshots = wiseOldManClient.lookupSnapshots(user, Period.WEEK);
      monthSnapshots = wiseOldManClient.lookupSnapshots(user, Period.MONTH);
      yearSnapshots = wiseOldManClient.lookupSnapshots(user, Period.YEAR);
    }

    int lineChartOffset = -1;
    int snapshotsSize = 0;
    if (fiveMinSnapshots != null)
      snapshotsSize = Math.max(snapshotsSize, fiveMinSnapshots.size());
    if (daySnapshots != null)
    snapshotsSize = Math.max(snapshotsSize, daySnapshots.size());
    if (weekSnapshots != null)
    snapshotsSize = Math.max(snapshotsSize, weekSnapshots.size());
    if (monthSnapshots != null)
    snapshotsSize = Math.max(snapshotsSize, monthSnapshots.size());
    if (yearSnapshots != null)
    snapshotsSize = Math.max(snapshotsSize, yearSnapshots.size());
    snapshotsText.setText(snapshotsSize + " snapshots");

    if (fiveMinSnapshots != null && fiveMinSnapshots.size() > 0) {
      if (lineChartOffset == -1)
        lineChartOffset = 135;
      else
        lineChartOffset += 400;
      final NumberAxis xAxis = new NumberAxis();
      final NumberAxis yAxis = new NumberAxis();
      xAxis.setLabel("Minute");
      xAxis.setTickLabelFill(Paint.valueOf("CYAN"));
      yAxis.setTickLabelFill(Paint.valueOf("CYAN"));
      xAxis.setForceZeroInRange(false);
      yAxis.setForceZeroInRange(false);
      final LineChart<Number,Number> lineChart =
          new LineChart<>(xAxis,yAxis);
      lineChart.setTitle("5Min");

      XYChart.Series series = new XYChart.Series();
      series.setName(currentSkill.getName());

      for (Snapshot s : fiveMinSnapshots) {
        double day = Double.parseDouble(s.createdAt.split("-")[1] + "." + Integer.parseInt(s.createdAt.split("-")[2].substring(0, 2)) * 3);
        series.getData().add(new XYChart.Data(day, s.getSnapshotFromSkill(currentSkill).xp));
      }

      lineChart.setLegendVisible(false);
      lineChart.setCreateSymbols(false);
      lineChart.setMaxWidth(340);
      lineChart.getData().add(series);
      lineChart.setLayoutY(lineChartOffset);
      lineChart.getStylesheets().add("css/xychart.css");
      Text displayNameLabel = new Text(skill.getName());
      displayNameLabel.setFill(Paint.valueOf("CYAN"));
      displayNameLabel.setLayoutX(5);
      displayNameLabel.setLayoutY(85);
      scrollPanel.getChildren().add(lineChart);
    }
    if (daySnapshots != null && daySnapshots.size() > 0) {
      if (lineChartOffset == -1)
        lineChartOffset = 135;
      else
        lineChartOffset += 400;
      final NumberAxis xAxis = new NumberAxis();
      final NumberAxis yAxis = new NumberAxis();
      xAxis.setLabel("Minute");
      xAxis.setTickLabelFill(Paint.valueOf("CYAN"));
      yAxis.setTickLabelFill(Paint.valueOf("CYAN"));
      xAxis.setForceZeroInRange(false);
      yAxis.setForceZeroInRange(false);
      final LineChart<Number,Number> lineChart =
          new LineChart<>(xAxis,yAxis);
      lineChart.setTitle("Day");

      XYChart.Series series = new XYChart.Series();
      series.setName(currentSkill.getName());

      for (Snapshot s : daySnapshots) {
        double day = Double.parseDouble(s.createdAt.split("-")[1] + "." + Integer.parseInt(s.createdAt.split("-")[2].substring(0, 2)) * 3);
        series.getData().add(new XYChart.Data(day, s.getSnapshotFromSkill(currentSkill).xp));
      }

      lineChart.setLegendVisible(false);
      lineChart.setCreateSymbols(false);
      lineChart.setMaxWidth(340);
      lineChart.getData().add(series);
      lineChart.setLayoutY(lineChartOffset);
      lineChart.getStylesheets().add("css/xychart.css");
      Text displayNameLabel = new Text(skill.getName());
      displayNameLabel.setFill(Paint.valueOf("CYAN"));
      displayNameLabel.setLayoutX(5);
      displayNameLabel.setLayoutY(85);
      scrollPanel.getChildren().add(lineChart);
    }
    if (weekSnapshots != null && weekSnapshots.size() > 0) {
      if (lineChartOffset == -1)
        lineChartOffset = 135;
      else
        lineChartOffset += 400;
      final NumberAxis xAxis = new NumberAxis();
      final NumberAxis yAxis = new NumberAxis();
      xAxis.setLabel("Minute");
      xAxis.setTickLabelFill(Paint.valueOf("CYAN"));
      yAxis.setTickLabelFill(Paint.valueOf("CYAN"));
      xAxis.setForceZeroInRange(false);
      yAxis.setForceZeroInRange(false);
      final LineChart<Number,Number> lineChart =
          new LineChart<>(xAxis,yAxis);
      lineChart.setTitle("Week");

      XYChart.Series series = new XYChart.Series();
      series.setName(currentSkill.getName());

      for (Snapshot s : weekSnapshots) {
        double day = Double.parseDouble(s.createdAt.split("-")[1] + "." + Integer.parseInt(s.createdAt.split("-")[2].substring(0, 2)) * 3);
        series.getData().add(new XYChart.Data(day, s.getSnapshotFromSkill(currentSkill).xp));
      }

      lineChart.setLegendVisible(false);
      lineChart.setCreateSymbols(false);
      lineChart.setMaxWidth(340);
      lineChart.getData().add(series);
      lineChart.setLayoutY(lineChartOffset);
      lineChart.getStylesheets().add("css/xychart.css");
      Text displayNameLabel = new Text(skill.getName());
      displayNameLabel.setFill(Paint.valueOf("CYAN"));
      displayNameLabel.setLayoutX(5);
      displayNameLabel.setLayoutY(85);
      scrollPanel.getChildren().add(lineChart);
    }
    if (monthSnapshots != null && monthSnapshots.size() > 0) {
      if (lineChartOffset == -1)
        lineChartOffset = 135;
      else
        lineChartOffset += 400;
      final NumberAxis xAxis = new NumberAxis();
      final NumberAxis yAxis = new NumberAxis();
      xAxis.setLabel("Minute");
      xAxis.setTickLabelFill(Paint.valueOf("CYAN"));
      yAxis.setTickLabelFill(Paint.valueOf("CYAN"));
      xAxis.setForceZeroInRange(false);
      yAxis.setForceZeroInRange(false);
      final LineChart<Number,Number> lineChart =
          new LineChart<>(xAxis,yAxis);
      lineChart.setTitle("Month");

      XYChart.Series series = new XYChart.Series();
      series.setName(currentSkill.getName());

      for (Snapshot s : monthSnapshots) {
        double day = Double.parseDouble(s.createdAt.split("-")[1] + "." + Integer.parseInt(s.createdAt.split("-")[2].substring(0, 2)) * 3);
        series.getData().add(new XYChart.Data(day, s.getSnapshotFromSkill(currentSkill).xp));
      }

      lineChart.setLegendVisible(false);
      lineChart.setCreateSymbols(false);
      lineChart.setMaxWidth(340);
      lineChart.getData().add(series);
      lineChart.setLayoutY(lineChartOffset);
      lineChart.getStylesheets().add("css/xychart.css");
      Text displayNameLabel = new Text(skill.getName());
      displayNameLabel.setFill(Paint.valueOf("CYAN"));
      displayNameLabel.setLayoutX(5);
      displayNameLabel.setLayoutY(85);
      scrollPanel.getChildren().add(lineChart);
    }
    if (yearSnapshots != null && yearSnapshots.size() > 0) {
      if (lineChartOffset == -1)
        lineChartOffset = 135;
      else
        lineChartOffset += 400;
      final NumberAxis xAxis = new NumberAxis();
      final NumberAxis yAxis = new NumberAxis();
      xAxis.setLabel("Month");
      xAxis.setTickLabelFill(Paint.valueOf("CYAN"));
      yAxis.setTickLabelFill(Paint.valueOf("CYAN"));
      xAxis.setForceZeroInRange(false);
      yAxis.setForceZeroInRange(false);
      final LineChart<Number,Number> lineChart =
          new LineChart<>(xAxis,yAxis);
      lineChart.setTitle("Year");

      XYChart.Series series = new XYChart.Series();
      series.setName(currentSkill.getName());

      for (Snapshot s : yearSnapshots) {
        double day = Double.parseDouble(s.createdAt.split("-")[1] + "." + Integer.parseInt(s.createdAt.split("-")[2].substring(0, 2)) * 3);
        series.getData().add(new XYChart.Data(day, s.getSnapshotFromSkill(currentSkill).xp));
      }

      lineChart.setLegendVisible(false);
      lineChart.setCreateSymbols(false);
      lineChart.setMaxWidth(340);
      lineChart.getData().add(series);
      lineChart.setLayoutY(lineChartOffset);
      lineChart.getStylesheets().add("css/xychart.css");
      Text displayNameLabel = new Text(skill.getName());
      displayNameLabel.setFill(Paint.valueOf("CYAN"));
      displayNameLabel.setLayoutX(5);
      displayNameLabel.setLayoutY(85);
      scrollPanel.getChildren().add(lineChart);
    }
  }
}
