package application;


import java.util.Random;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Main extends Application {

    private Timeline timeline;

    private DoubleProperty timeSeconds;
    private Duration time = Duration.ZERO;

    static Random random = new Random();

    Layer playfield;

    ObservableList<Attractor> allAttractors = FXCollections.observableArrayList();

    ObservableList<Vehicle> allVehicles = FXCollections.observableArrayList();

    AnimationTimer gameLoop;

    Vector2D mouseLocation = new Vector2D(0, 0);

    Scene scene;

    MouseGestures mouseGestures = new MouseGestures();

    SimpleStringProperty cnt;
    SimpleStringProperty kill;
    SimpleStringProperty seconds;


    @Override
    public void start(Stage primaryStage) {

        //help
        Button helpButton = new Button("Help");


        helpButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                Stage helpWindow = new Stage();
                // New help window (Stage)
                helpWindow.initOwner(primaryStage);
                helpWindow.initModality(Modality.APPLICATION_MODAL);
                // Create the WebView
                WebView webView = new WebView();

                // Create the WebEngine
                final WebEngine webEngine = webView.getEngine();

                // set text
                String helpHtml = "<!DOCTYPE html>" +
                        "<html><title>Help!</title>" +
                        "<body><h1>Справка по игре Balls'n'Arrows</h1>" +
                        "<h2>Стрелы и шары</h2></html>" +
                        "<p>Cуть игры в том, чтобы собрать как можно больше очков, кликая на шарики, которые появляются в разных местах окна, до тех пор, пока шарики не поразят стрелы " +
                        "Стрелы ускоряются и замедляются, и всегда летят в сторону цели. У каждой стрелы своя цель, при поражении одной цели, стрела захватывает следующую и летит к ней." +
                        "Когда стрелы поразят количество шаров, равное лимиту, игра закончится. Игру можно начать заново." +
                        "<p>При входе в игру вы видите главное окно. В левом верхнем углу - готовые шары и стрелы. Количество стрел и шаров отображено в двух таблицах справа, верхняя - для стрел, нижняя - для шаров. " +
                        "<p>В таблице стрел отражены три значения - номер цели в списке целей, счетчик пораженных целей и счетчик пораженных целей-дураков, которые просто были у стрелы на пути, пока она летела к своей цели." +
                        "все счетчики суммируются в втором основном счетчике в разделе Score. " +
                        "<p>В таблице шаров отражены координаты их местонахождения, количество поражений от стрел и количество кользовательских кликов - очков. Клики пользователя суммируются в первом счетчике в разделе Score." +
                        "<p> Также в разделе Score имеются кнопки начала и паузы игры(play, stop), а также кнопка Respawn, которая начинает новую игру с применением настроек, если они были отредактированы. " +
                        "Также в этом разделе имеется таймер красного цвета, и кнопка help, которую вы уже нашли, если вы видите это окно." +
                        "<p>В настройках справа помимо двух таблиц, имеются настройки количества шаров, стрел, максимальная скорость и ускорение стрелы, и лимит пораженных стрелами шаров, после которого сеанс игры закончится." +
                        "" +
                        "<p>Для продолжения игры просто закройте это окно." +
                        "</body>" +
                        "</html>";

                webEngine.loadContent(helpHtml);

                // Update the stage title when a new web page title is available
                webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>()
                {
                    public void changed(ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState)
                    {
                        if (newState == Worker.State.SUCCEEDED)
                        {
                            //stage.setTitle(webEngine.getLocation());
                            helpWindow.setTitle(webEngine.getTitle());
                        }
                    }
                });

                // Create the VBox
                VBox root = new VBox();
                // Add the WebView to the VBox
                root.getChildren().add(webView);

                // Set the Style-properties of the VBox
                root.setStyle("-fx-padding: 10;" +
                        "-fx-border-style: solid inside;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-insets: 5;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-color: blue;");

                // Create the Scene
                Scene helpScene = new Scene(root, 500, 500);

                helpWindow.setTitle("Help");
                helpWindow.setScene(helpScene);

                // Set position of second window, related to primary window.
                helpWindow.setX(primaryStage.getX() + 200);
                helpWindow.setY(primaryStage.getY() + 100);

                helpWindow.show();
            }
        });


        // create timer
        timeSeconds = new SimpleDoubleProperty();
        Label timerLabel = new Label();
        timerLabel.textProperty().bind(timeSeconds.asString());
        timerLabel.setTextFill(Color.RED);
        timerLabel.setStyle(Settings.scoreStyle);
        timeline = new Timeline(
                new KeyFrame(Duration.millis(100),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent t) {
                                Duration duration = ((KeyFrame) t.getSource()).getTime();
                                time = time.add(duration);
                                timeSeconds.set(time.toSeconds());
                            }

                        })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);


        // create containers and all the stuff in the window

        BorderPane root = new BorderPane();


        Label scoreLabel = new Label("Scores");
        scoreLabel.setStyle(Settings.scoreStyle);

        cnt = new SimpleStringProperty(Integer.toString(0));
        seconds = new SimpleStringProperty(Integer.toString(0));
        kill = new SimpleStringProperty(Integer.toString(0));
        Label userCountLabel = new Label();
        userCountLabel.setStyle(Settings.scoreStyle);
        userCountLabel.textProperty().bind(cnt);
        Label secondsLabel = new Label();
        secondsLabel.setStyle(Settings.scoreStyle);
        secondsLabel.textProperty().bind(seconds);
        Label killLabel = new Label();
        killLabel.setStyle(Settings.scoreStyle);
        killLabel.textProperty().bind(kill);
        Button playButton = new Button("Play");

        playButton.setDisable(false);
        Button stopButton = new Button("Stop");
        stopButton.setDisable(true);

        Button respawnButton = new Button("Respawn");
        respawnButton.setOnAction(event -> {
            timeline.stop();
            timeSeconds.setValue(0);
            playButton.setDisable(true);
            stopButton.setDisable(false);
            gameLoop.stop();
            respawnAll();
            gameLoop.start();
            timeline.play();
        });

        playButton.setOnAction(event -> {
            timeSeconds.setValue(0);
            gameLoop.start();
            timeline.play();
            playButton.setDisable(true);
            stopButton.setDisable(false);
        });


        stopButton.setOnAction(event -> {

            gameLoop.stop();
            timeline.stop();

            playButton.setDisable(false);
            stopButton.setDisable(true);
        });

        HBox userCountHBox = new HBox(30, scoreLabel, userCountLabel, killLabel, playButton, stopButton, respawnButton, timerLabel, helpButton);
        userCountHBox.setAlignment(Pos.CENTER_RIGHT);
        userCountHBox.setPadding(new Insets(5,30,5,30));
        scene = new Scene(root, Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT);

        // playfield for our Sprites
        playfield = new Layer(Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT);

        // entire game as layers
        Pane layerPane = new Pane();
        layerPane.getChildren().addAll(playfield);

        TextField atc = new TextField(Integer.toString(Settings.ATTRACTOR_COUNT));
        atc.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("textfield 'Attractors' count' changed from " + oldValue + " to " + newValue);
            if (oldValue != newValue) {
                try {
                    Settings.ATTRACTOR_COUNT = Integer.parseInt(newValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        TextField vc = new TextField(Integer.toString(Settings.VEHICLE_COUNT));
        vc.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("textfield 'Vehicle_count' changed from " + oldValue + " to " + newValue);
            if (oldValue != newValue) {
                try {
                    Settings.VEHICLE_COUNT = Integer.parseInt(newValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        TextField ms = new TextField(Double.toString(Settings.SPRITE_MAX_SPEED));
        ms.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("textfield 'Max_speed' changed from " + oldValue + " to " + newValue);
            if (oldValue != newValue) {
                try {
                    Settings.SPRITE_MAX_SPEED = Double.parseDouble(newValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        TextField mf = new TextField(Double.toString(Settings.SPRITE_MAX_FORCE));
        mf.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("textfield 'Max_Force' changed from " + oldValue + " to " + newValue);
            if (oldValue != newValue) {
                try {
                    Settings.SPRITE_MAX_FORCE = Double.parseDouble(newValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        TextField kl = new TextField(Double.toString(Settings.KILL_LIMIT));
        mf.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("textfield 'Kill limit' changed from " + oldValue + " to " + newValue);
            if (oldValue != newValue) {
                try {
                    Settings.KILL_LIMIT = Integer.parseInt(newValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        GridPane settingsGridPane = new GridPane();
        settingsGridPane.setPadding(new Insets(5));
        settingsGridPane.setHgap(25);
        settingsGridPane.setVgap(15);
        settingsGridPane.add(new Label("Balls    "), 0, 0);
        settingsGridPane.add(new Label("Arrows   "), 0, 1);
        settingsGridPane.add(new Label("Max speed"), 0, 2);
        settingsGridPane.add(new Label("Max force"), 0, 3);
        settingsGridPane.add(new Label("Kill limit"), 0, 4);
        settingsGridPane.add(atc, 1, 0);
        settingsGridPane.add(vc, 1, 1);
        settingsGridPane.add(ms, 1, 2);
        settingsGridPane.add(mf, 1, 3);
        settingsGridPane.add(kl, 1, 4);

        TableView<Vehicle> vehiclesTableView = new TableView<>();
        TableColumn<Vehicle, Integer> vehicleColumn1 = new TableColumn<Vehicle, Integer>("Target Index");
        vehicleColumn1.setCellValueFactory(new PropertyValueFactory<Vehicle, Integer>("targetIndex"));

        TableColumn<Vehicle, Integer> vehicleColumn2 = new TableColumn<Vehicle, Integer>("Kill Count");
        vehicleColumn2.setCellValueFactory(new PropertyValueFactory<>("killCount"));

        TableColumn<Vehicle, Integer> vehicleColumn3 = new TableColumn<Vehicle, Integer>("Fool Count");
        vehicleColumn3.setCellValueFactory(new PropertyValueFactory<>("foolCount"));

        vehiclesTableView.getColumns().addAll(vehicleColumn1, vehicleColumn2, vehicleColumn3);

        vehiclesTableView.setItems(allVehicles);
        TableView<Attractor> attractorsTableView = new TableView<>();
        TableColumn<Attractor, Integer> attractorColumn1 = new TableColumn<Attractor, Integer>("X");
        attractorColumn1.setCellValueFactory(new PropertyValueFactory<>("x"));
        TableColumn<Attractor, Integer> attractorColumn2 = new TableColumn<Attractor, Integer>("Y");
        attractorColumn2.setCellValueFactory(new PropertyValueFactory<>("y"));
        TableColumn<Attractor, Integer> attractorColumn3 = new TableColumn<Attractor, Integer>("Respawn");
        attractorColumn3.setCellValueFactory(new PropertyValueFactory<>("killCount"));
        TableColumn<Attractor, Integer> attractorColumn4 = new TableColumn<Attractor, Integer>("User Click");
        attractorColumn4.setCellValueFactory(new PropertyValueFactory<>("userClickCount"));

        attractorsTableView.getColumns().addAll(attractorColumn1, attractorColumn2, attractorColumn3, attractorColumn4);
        attractorsTableView.setItems(allAttractors);

        VBox vBox = new VBox(20, settingsGridPane, vehiclesTableView, attractorsTableView);
        vBox.setPrefSize(550, 720);
        vBox.setPadding(new Insets(15, 15, 15, 15));
        HBox hbox = new HBox(layerPane, vBox);
        root.setTop(userCountHBox);
        root.setCenter(hbox);
        root.setStyle("-fx-background-color: lightblue");


        // add content
        // add vehicles
        for (int i = 0; i < Settings.VEHICLE_COUNT; i++) {
            addVehicles();
        }

        // add attractors
        for (int i = 0; i < Settings.ATTRACTOR_COUNT; i++) {
            addAttractors();
        }

        // add mouse location listener
        // capture mouse position
        scene.addEventFilter(MouseEvent.ANY, e -> {
            mouseLocation.set(e.getX(), e.getY());
        });

        // move attractors via mouse
        for (Attractor attractor : allAttractors) {
            mouseGestures.makeDraggable(attractor);
        }


        // run animation loop
        gameLoop = new AnimationTimer() {


            @Override
            public void handle(long now) {
                if (now % 60 == 0) seconds.setValue(Integer.toString(Integer.parseInt(seconds.getValue()) + 1));

                // seek attractor location, apply force to get towards it

                for (Vehicle vehicle : allVehicles) {

                    Attractor attractor = allAttractors.get(vehicle.getTargetIndex());

                    attractor.setOnMouseClicked(event -> {
                        incScores();
                        attractor.incUserClickCount();
                        System.out.println("Inc " + attractor.getUserClickCount());
                        moveAttractor(attractor);
                    });

                    //System.out.println(allVehicles.indexOf(vehicle) + " " + vehicle.getLocation().x + " " + vehicle.getLocation().y + " " + Vector2D.subtract(vehicle.getLocation(), attractor.getLocation()).magnitude() + " " + allAttractors.indexOf(attractor));
                    vehicle.seek(attractor.getLocation());

                    // move sprite
                    allVehicles.forEach(Sprite::move);

                    // update in fx scene
                    allVehicles.forEach(Sprite::display);
                    allAttractors.forEach(Sprite::display);

                    //check collisions with target


                    if (Vector2D.subtract(vehicle.getLocation(), attractor.getLocation()).magnitude() < 50) {

                        moveAttractor(attractor);

                        attractor.incKillcount();
                        incKill();
                        vehicle.incTargetIndex();
                        System.out.println("Killed and respawn " + allAttractors.indexOf(attractor) + " " + attractor.getKillCount());
                        if (vehicle.getTargetIndex() == allAttractors.size()) vehicle.setTargetIndex(0);
                        vehicle.incKillCount();
                        System.out.println("#" + allVehicles.indexOf(vehicle) + " k-" + vehicle.getKillCount());
                    }

                    //check collision with fools
                    for (Attractor a : allAttractors
                            ) {
                        if (Vector2D.subtract(vehicle.getLocation(), a.getLocation()).magnitude() < 50) {
                            vehicle.incFoolCount();
                            a.incKillcount();
                            incKill();
                            System.out.println("Killed and respawn " + allAttractors.indexOf(a) + " " + a.getKillCount());
                            System.out.println("#" + allVehicles.indexOf(vehicle) + " k-" + vehicle.getKillCount() + "  f-" + vehicle.getFoolCount());
                            moveAttractor(a);

                        }
                    }
                    if (vehicle.getKillCount() + vehicle.getFoolCount() == 10) {
                        vehicle.modifyColor(Color.RED);

                    } else if (vehicle.getKillCount() + vehicle.getFoolCount() == 20) {
                        vehicle.modifyColor(Color.GREEN);
                    } else if (vehicle.getKillCount() + vehicle.getFoolCount() == 30) {
                        vehicle.modifyColor(Color.CHARTREUSE);
                    }


                    if (Integer.parseInt(kill.getValue()) == Settings.KILL_LIMIT) {

                        this.stop();
                        timeline.stop();

                        playButton.setDisable(true);
                        stopButton.setDisable(true);
                        System.out.println(now + " ");

                    }

                }


            }

        };

        //gameLoop.start();
        primaryStage.setTitle(Settings.GAME_NAME);

        primaryStage.setScene(scene);

        primaryStage.show();


    }


    /**
     * Add single vehicle to list of vehicles and to the playfield
     */
    private void addVehicles() {

        Layer layer = playfield;

        // random location
        double x = random.nextDouble() * (layer.getWidth() - 100);
        double y = random.nextDouble() * (layer.getHeight() - 100);

        // dimensions
        double width = 20;
        double height = width / 2.0;

        // create vehicle data
        Vector2D location = new Vector2D(x, y);
        Vector2D velocity = new Vector2D(0, 0);
        Vector2D acceleration = new Vector2D(0, 0);

        // create sprite and add to layer
        Vehicle vehicle = new Vehicle(layer, location, velocity, acceleration, width, height);

        // register vehicle
        allVehicles.add(vehicle);

    }

    // i don't know why i 'rote diz' methods
    private void removeVehicle(int index) {
        allVehicles.remove(index);
    }

    private void removeAttractor(int index) {
        allAttractors.remove(index);
    }

    private void removeVehicle() {
        allVehicles.remove(0);
    }

    private void removeAttractor() {
        allAttractors.remove(0);
    }

    //respawn attractors
    private void moveAttractor(Attractor attractor) {
        Layer layer = playfield;
        double x = random.nextDouble() * (layer.getWidth() - 100);
        double y = random.nextDouble() * (layer.getHeight() - 100);
        attractor.setLocation(x, y);
        attractor.updateLocation();

    }


    private void addAttractors() {

        Layer layer = playfield;

        // center attractor
        //double x = layer.getWidth() / 2;
        //double y = layer.getHeight() / 2;
        double x = random.nextDouble() * (layer.getWidth() - 100);
        double y = random.nextDouble() * (layer.getHeight() - 100);


        // dimensions
        double width = 50;
        double height = 50;

        // create attractor data
        Vector2D location = new Vector2D(x, y);
        Vector2D velocity = new Vector2D(0, 0);
        Vector2D acceleration = new Vector2D(0, 0);

        // create attractor and add to layer
        Attractor attractor = new Attractor(layer, location, velocity, acceleration, width, height);

        // register sprite
        allAttractors.add(attractor);

    }
    //increment methods
    private void incScores() {
        cnt.setValue(Integer.toString(Integer.parseInt(cnt.getValue()) + 1));
    }

    private void incKill() {
        kill.setValue(Integer.toString(Integer.parseInt(kill.getValue()) + 1));
    }

    //respawn all units and replay the game
    private void respawnAll() {

        playfield.getChildren().clear();
        allAttractors.clear();
        allVehicles.clear();
        cnt.setValue(Integer.toString(0));
        kill.setValue(Integer.toString(0));

        // add vehicles
        for (int i = 0; i < Settings.VEHICLE_COUNT; i++) {
            addVehicles();
        }

        // add attractors
        for (int i = 0; i < Settings.ATTRACTOR_COUNT; i++) {
            addAttractors();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}