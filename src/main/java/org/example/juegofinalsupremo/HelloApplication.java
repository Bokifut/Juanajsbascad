package org.example.juegofinalsupremo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.juegofinalsupremo.data.Cola;
import org.example.juegofinalsupremo.data.Lista;
import org.example.juegofinalsupremo.exceptions.GameException;
import org.example.juegofinalsupremo.io.GameJsonRepository;
import org.example.juegofinalsupremo.model.Cell;
import org.example.juegofinalsupremo.model.CharacterClass;
import org.example.juegofinalsupremo.model.Direction;
import org.example.juegofinalsupremo.model.Enemy;
import org.example.juegofinalsupremo.model.GameEngine;
import org.example.juegofinalsupremo.model.GameObject;
import org.example.juegofinalsupremo.model.GameState;
import org.example.juegofinalsupremo.model.Position;
import org.example.juegofinalsupremo.model.Room;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.stream.Stream;

public class HelloApplication extends Application {
    private static final String SAVE_PATH = "partida-guardada.json";
    private static final String[] DIFFICULTIES = {"Facil", "Medio", "Dificil", "Marquette"};
    private static final CharacterClass[] CHARACTERS = CharacterClass.values();

    private Stage primaryStage;
    private BorderPane gameRoot;
    private GameEngine engine;
    private GridPane mapGrid;
    private Label roomLabel;
    private Label turnLabel;
    private Label selectedDirectionLabel;
    private Label playerTypeLabel;
    private Label attackLabel;
    private Label defenseLabel;
    private Label speedLabel;
    private Label coinsLabel;
    private Label healthTextLabel;
    private Label inventoryDetailLabel;
    private ProgressBar healthBar;
    private ListView<String> inventoryList;
    private ListView<String> forgeInventoryList;
    private Lista<Integer> forgeInventoryIndexes;
    private TextArea logArea;
    private Image napoleonImage;
    private Image[] characterImages;
    private Image[] enemyImages;
    private Image floorImage;
    private Image trapImage;
    private Image potionImage;
    private Image coinImage;
    private Image weaponImage;
    private Image doorInfernoImage;
    private Image doorFinalImage;
    private Image doorClosedImage;
    private Image lockedDoorClosedImage;
    private Image doorOpenImage;
    private Image finalDoorClosedImage;
    private Image lockedDoorOpenImage;
    private Image blacksmithImage;
    private Image marquettePhaseTwoImage;
    private Image starterShieldImage;
    private Image starterDaggerImage;
    private Image starterCetroImage;
    private Image starterSwordImage;
    private Image starterTomahawkImage;
    private MediaPlayer backgroundPlayer;
    private String currentBackgroundMusic;
    private String currentSavePath = SAVE_PATH;
    private int selectedDifficultyIndex = 1;
    private int selectedCharacterIndex;
    private int currentCellSize = 64;
    private boolean enemyHintShown;
    private Direction selectedDirection = Direction.UP;
    private Lista<Position> routeHintCells = new Lista<Position>();
    private int selectedForgeInventoryIndex = -1;
    private boolean finalRouteHintBought;
    private int warningLogLineCursor;
    private double volumeLevel = 0.75;
    private long lastAmbientSoundMillis;
    private final Random soundRandom = new Random();
    private final Lista<String> blacksmithHintRooms = new Lista<String>();
    private KeyCode moveUpKey = KeyCode.W;
    private KeyCode moveLeftKey = KeyCode.A;
    private KeyCode moveDownKey = KeyCode.S;
    private KeyCode moveRightKey = KeyCode.D;
    private KeyCode openDoorKey = KeyCode.E;
    private KeyCode pickUpKey = KeyCode.R;
    private KeyCode passTurnKey = KeyCode.X;
    private KeyCode inventoryKey = KeyCode.I;
    private KeyCode attackKey = KeyCode.Q;
    private KeyCode aimUpKey = KeyCode.UP;
    private KeyCode aimLeftKey = KeyCode.LEFT;
    private KeyCode aimDownKey = KeyCode.DOWN;
    private KeyCode aimRightKey = KeyCode.RIGHT;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        loadImages();
        stage.setTitle("Juego Final Supremo");
        showMainMenu();
        stage.show();
    }

    private void loadImages() {
        napoleonImage = image("/assets/napoleon.png");
        characterImages = new Image[CHARACTERS.length];
        characterImages[0] = potionImageFile("Ladron.png");
        characterImages[1] = potionImageFile("Vikingo.png");
        characterImages[2] = potionImageFile("Guerrero.png");
        characterImages[3] = potionImageFile("Acorazado.png");
        characterImages[4] = potionImageFile("Clero.png");
        characterImages[5] = napoleonImage;
        enemyImages = new Image[] {
                potionImageFile("ChatGPT_Image_20_may_2026_21_45_23.png"),
                imageFile("Duende - copia.png", "/assets/enemies/duende.png"),
                potionImageFile("Dragon.png"),
                potionImageFile("Orco.png"),
                potionImageFile("Final_boss.png")
        };
        floorImage = potionImageFile("Suelo.png");
        trapImage = image("/assets/tiles/trap.png");
        potionImage = potionImageFile("Pocion_vida_pequena.png");
        coinImage = potionImageFile("monedas.png");
        weaponImage = potionImageFile("Espada_normal.png");
        doorInfernoImage = potionImageFile("Puerta_infierno.png");
        doorFinalImage = potionImageFile("Puerta_final.png");
        doorClosedImage = potionImageFile("puerta normal cerrada.png");
        lockedDoorClosedImage = potionImageFile("puerta cerrada.png");
        doorOpenImage = potionImageFile("puerta de la llave abierta.png");
        finalDoorClosedImage = potionImageFile("puerta final cerrada.png");
        lockedDoorOpenImage = potionImageFile("puerta de la llave abierta.png");
        blacksmithImage = potionImageFile("herrero.png");
        marquettePhaseTwoImage = potionImageFile("marquette fase 2.png");
        starterShieldImage = potionImageFile("escudo.png");
        starterDaggerImage = potionImageFile("daga.png");
        starterCetroImage = potionImageFile("cetro.png");
        starterSwordImage = potionImageFile("espada guerrero.png");
        starterTomahawkImage = potionImageFile("tomahack.png");
    }

    private Image image(String path) {
        return new Image(getClass().getResource(path).toExternalForm());
    }

    private Image imageFile(String fileName, String fallbackResource) {
        Path path = findImageFile(fileName);
        if (path != null) {
            return new Image(path.toUri().toString());
        }
        return image(fallbackResource);
    }

    private Image potionImageFile(String fileName) {
        Path found = findImageFile(fileName);
        if (found != null) {
            return new Image(found.toUri().toString());
        }
        Path path = Paths.get("Imágenes", "Poción", fileName);
        if (Files.exists(path)) {
            return new Image(path.toUri().toString());
        }
        return potionImage == null ? image("/assets/objects/potion.png") : potionImage;
    }

    private Path findImageFile(String fileName) {
        try (Stream<Path> paths = Files.walk(Paths.get("."))) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equalsIgnoreCase(fileName))
                    .findFirst()
                    .orElse(null);
        } catch (IOException ex) {
            return null;
        }
    }

    private String screenBackground(String fileName) {
        Path path = findImageFile(fileName);
        String base = "-fx-background-color: #130a0a; -fx-background-size: cover; "
                + "-fx-background-position: center center; -fx-background-repeat: no-repeat;";
        if (path == null) {
            return base;
        }
        return base + " -fx-background-image: url('" + path.toUri().toString() + "');";
    }

    private void styleGameButton(Button button, int fontSize, int radius) {
        String base = gameButtonStyle(fontSize, radius, false);
        String hover = gameButtonStyle(fontSize, radius, true);
        button.setStyle(base);
        button.setOnMouseEntered(event -> button.setStyle(hover));
        button.setOnMouseExited(event -> button.setStyle(base));
    }

    private void styleSkyButton(Button button, int fontSize, int radius) {
        String base = skyButtonStyle(fontSize, radius, false);
        String hover = skyButtonStyle(fontSize, radius, true);
        button.setStyle(base);
        button.setOnMouseEntered(event -> button.setStyle(hover));
        button.setOnMouseExited(event -> button.setStyle(base));
    }

    private String gameButtonStyle(int fontSize, int radius, boolean hover) {
        String background = hover ? "#b81818" : "#7f1111";
        String border = hover ? "#ffd166" : "#4a0606";
        String glow = hover ? " -fx-effect: dropshadow(gaussian, rgba(255, 209, 102, 0.85), 16, 0.35, 0, 0);" : "";
        return "-fx-background-color: linear-gradient(#d63a24, " + background + ");"
                + " -fx-text-fill: #fff4e6;"
                + " -fx-font-size: " + fontSize + "px;"
                + " -fx-font-weight: bold;"
                + " -fx-background-radius: " + radius + ";"
                + " -fx-border-radius: " + radius + ";"
                + " -fx-border-color: " + border + ";"
                + " -fx-border-width: 3;"
                + " -fx-padding: 8 14 8 14;"
                + " -fx-cursor: hand;"
                + glow;
    }

    private String skyButtonStyle(int fontSize, int radius, boolean hover) {
        String background = hover ? "#4fc3ff" : "#1f9ed8";
        String border = hover ? "#d8f7ff" : "#0a5e86";
        String glow = hover ? " -fx-effect: dropshadow(gaussian, rgba(180, 236, 255, 0.9), 16, 0.35, 0, 0);" : "";
        return "-fx-background-color: linear-gradient(#9be7ff, " + background + ");"
                + " -fx-text-fill: #062436;"
                + " -fx-font-size: " + fontSize + "px;"
                + " -fx-font-weight: bold;"
                + " -fx-background-radius: " + radius + ";"
                + " -fx-border-radius: " + radius + ";"
                + " -fx-border-color: " + border + ";"
                + " -fx-border-width: 3;"
                + " -fx-padding: 8 14 8 14;"
                + " -fx-cursor: hand;"
                + glow;
    }

    private Image createCoinImage() {
        WritableImage image = new WritableImage(48, 48);
        for (int y = 0; y < 48; y++) {
            for (int x = 0; x < 48; x++) {
                double dx = x - 24;
                double dy = y - 24;
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance <= 19) {
                    image.getPixelWriter().setColor(x, y, distance > 15 ? Color.GOLDENROD : Color.GOLD);
                } else {
                    image.getPixelWriter().setColor(x, y, Color.TRANSPARENT);
                }
            }
        }
        return image;
    }

    private void showMainMenu() {
        VBox menu = new VBox(18);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(40));
        menu.setStyle(screenBackground("pantalla menu principal.png"));

        Button newGame = menuButton("Nueva partida");
        newGame.setOnAction(event -> showNewGameSetup());

        Button continueGame = menuButton("Continuar partida");
        continueGame.setOnAction(event -> showLoadGameScreen());

        Button options = menuButton("Opciones");
        options.setOnAction(event -> showOptionsMenu());

        menu.getChildren().addAll(newGame, continueGame, options);

        Scene scene = new Scene(menu, 980, 640);
        playBackgroundMusic("musica de fondo no partida.mp3");
        applyScene(scene);
    }

    private Button menuButton(String text) {
        Button button = new Button(text);
        button.setMinWidth(340);
        button.setMinHeight(72);
        styleGameButton(button, 24, 8);
        return button;
    }

    private void applyScene(Scene scene) {
        boolean keepFullScreen = primaryStage != null && primaryStage.isFullScreen();
        boolean keepMaximized = primaryStage != null && primaryStage.isMaximized();
        primaryStage.setScene(scene);
        if (keepMaximized) {
            primaryStage.setMaximized(true);
        }
        if (keepFullScreen) {
            primaryStage.setFullScreen(true);
            Platform.runLater(() -> primaryStage.setFullScreen(true));
        }
    }

    private void showNewGameSetup() {
        selectedDifficultyIndex = 1;
        selectedCharacterIndex = 0;

        VBox setup = new VBox(10);
        setup.setAlignment(Pos.CENTER);
        setup.setPadding(new Insets(20));
        setup.setStyle(screenBackground("pantalla de juego nuevo.png"));

        Label title = new Label("Nueva partida");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 34px; -fx-font-weight: bold;");

        TextField saveNameField = setupField("Nombre de partida");
        TextField playerNameField = setupField("Nombre de jugador");

        Label characterStats = new Label();
        characterStats.setWrapText(true);
        characterStats.setMinWidth(420);
        characterStats.setMaxWidth(420);
        characterStats.setMinHeight(160);
        characterStats.setStyle("-fx-text-fill: #f2f5f8; -fx-font-size: 16px; -fx-background-color: #202631; "
                + "-fx-padding: 12; -fx-background-radius: 8; -fx-border-color: #3a4658; -fx-border-radius: 8;");
        ImageView characterPreview = setupPreview(characterImageForSelection());
        ImageView starterPreview = setupPreview(starterImage(selectedCharacterClass()));
        VBox imageBox = new VBox(8, characterPreview, starterPreview);
        imageBox.setAlignment(Pos.CENTER);
        HBox statsBox = new HBox(14, imageBox, characterStats);
        statsBox.setAlignment(Pos.CENTER);

        Button difficultyButton = setupSelectorButton("Dificultad: < " + DIFFICULTIES[selectedDifficultyIndex] + " >");
        difficultyButton.setOnAction(event -> {
            selectedDifficultyIndex = nextIndex(selectedDifficultyIndex, DIFFICULTIES.length);
            difficultyButton.setText("Dificultad: < " + DIFFICULTIES[selectedDifficultyIndex] + " >");
        });

        Button characterButton = setupSelectorButton("Personaje: < " + selectedCharacterClass().getDisplayName() + " >");
        characterStats.setText(characterSetupText(selectedCharacterClass()));
        characterButton.setOnAction(event -> {
            selectedCharacterIndex = nextIndex(selectedCharacterIndex, CHARACTERS.length);
            characterButton.setText("Personaje: < " + selectedCharacterClass().getDisplayName() + " >");
            characterStats.setText(characterSetupText(selectedCharacterClass()));
            characterPreview.setImage(characterImageForSelection());
            starterPreview.setImage(starterImage(selectedCharacterClass()));
        });

        Button start = menuButton("Empezar");
        start.setOnAction(event -> startConfiguredGame(saveNameField.getText(), playerNameField.getText()));

        Button back = setupSelectorButton("Volver");
        back.setOnAction(event -> showMainMenu());

        setup.getChildren().addAll(title, saveNameField, playerNameField,
                difficultyButton, characterButton,
                statsBox, start, back);
        playBackgroundMusic("musica de fondo no partida.mp3");
        applyScene(new Scene(setup, 1100, 760));
    }

    private ImageView setupPreview(Image image) {
        ImageView view = new ImageView(image);
        view.setFitWidth(86);
        view.setFitHeight(86);
        view.setPreserveRatio(true);
        view.setSmooth(true);
        return view;
    }

    private String characterSetupText(CharacterClass characterClass) {
        return "Stats base"
                + "\nVida: " + characterClass.getHealth()
                + " | Ataque: " + characterClass.getAttack()
                + " | Defensa: " + characterClass.getDefense()
                + " | Velocidad: " + characterClass.getSpeed()
                + "\n\nObjeto inicial"
                + "\n" + characterClass.getWeaponName()
                + " | Dano: " + signed(characterClass.getWeaponDamageBonus())
                + " | Defensa: " + signed(characterClass.getWeaponDefenseBonus())
                + " | " + characterClass.getWeaponCombatType();
    }

    private String signed(int value) {
        return value > 0 ? "+" + value : String.valueOf(value);
    }

    private TextField setupField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setMaxWidth(420);
        field.setMinHeight(54);
        field.setStyle("-fx-font-size: 20px; -fx-background-radius: 8; -fx-padding: 10;");
        return field;
    }

    private Button setupSelectorButton(String text) {
        Button button = new Button(text);
        button.setMinWidth(420);
        button.setMinHeight(58);
        styleGameButton(button, 20, 8);
        return button;
    }

    private int nextIndex(int current, int length) {
        return (current + 1) % length;
    }

    private void startConfiguredGame(String saveName, String playerName) {
        if (saveName == null || saveName.trim().isEmpty() || playerName == null || playerName.trim().isEmpty()) {
            showError("Rellena nombre de partida y nombre de personaje");
            return;
        }
        String normalizedPlayerName = normalizePlayerName(playerName);
        currentSavePath = savePathFor(saveName);
        enemyHintShown = false;
        finalRouteHintBought = false;
        warningLogLineCursor = 0;
        blacksmithHintRooms.clear();
        engine = GameEngine.sampleGame(normalizedPlayerName, selectedCharacterClass(), selectedDifficultyIndex);
        engine.getState().setFinalRouteHintBought(false);
        engine.getState().getLog().add("Partida configurada: " + currentSavePath);
        engine.getState().getLog().add("Dificultad seleccionada: " + DIFFICULTIES[selectedDifficultyIndex]);
        engine.getState().getLog().add("Personaje seleccionado: " + CHARACTERS[selectedCharacterIndex]);
        showGame();
    }

    private String normalizePlayerName(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            return "Heroe";
        }
        return playerName.trim();
    }

    private String savePathFor(String saveName) {
        if (saveName == null || saveName.trim().isEmpty()) {
            return SAVE_PATH;
        }
        String cleaned = saveName.trim().replaceAll("[^A-Za-z0-9_-]", "_");
        if (cleaned.isEmpty()) {
            return SAVE_PATH;
        }
        if (!cleaned.endsWith(".json")) {
            cleaned = cleaned + ".json";
        }
        return cleaned;
    }

    private int inferCharacterIndexFromInventory() {
        if (engine == null || engine.getState() == null) {
            return selectedCharacterIndex;
        }
        Lista<GameObject> inventory = engine.getState().getPlayer().getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            String id = inventory.get(i).getId().toLowerCase();
            if (id.startsWith("starter-")) {
                String characterName = id.substring("starter-".length()).toUpperCase();
                int forged = characterName.indexOf("-FORGED-");
                if (forged >= 0) {
                    characterName = characterName.substring(0, forged);
                }
                for (int index = 0; index < CHARACTERS.length; index++) {
                    if (CHARACTERS[index].name().equals(characterName)) {
                        return index;
                    }
                }
            }
        }
        return selectedCharacterIndex;
    }

    private void continueGame(String savePath) {
        try {
            engine = new GameEngine(new GameJsonRepository().load(savePath));
            currentSavePath = savePath;
            selectedCharacterIndex = inferCharacterIndexFromInventory();
            enemyHintShown = false;
            finalRouteHintBought = engine.getState().isFinalRouteHintBought();
        routeHintCells = finalRouteHintBought ? calculateCurrentRoomRouteHint() : new Lista<Position>();
            warningLogLineCursor = 0;
            blacksmithHintRooms.clear();
            engine.getState().getLog().add("Partida cargada desde " + savePath);
            if (engine.getState().isFinished()) {
                showEndScreen(engine.getState().isWon());
            } else {
                showGame();
            }
        } catch (Exception ex) {
            showError("No se pudo continuar la partida: " + ex.getMessage());
        }
    }

    private void showLoadGameScreen() {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle(screenBackground("pantalla de cargar partida y de opciones del menu principal.png"));

        Label title = new Label("Cargar Partida");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 34px; -fx-font-weight: bold;");

        ListView<String> savedGames = new ListView<>();
        savedGames.setMaxWidth(420);
        savedGames.setStyle("-fx-control-inner-background: #202631; -fx-font-size: 13px;");
        loadSavedGames(savedGames);

        Button loadButton = menuButton("Cargar");
        loadButton.setOnAction(event -> {
            String selectedGame = savedGames.getSelectionModel().getSelectedItem();
            if (selectedGame != null) {
                continueGame(selectedGame);
            } else {
                showError("Selecciona una partida para cargar.");
            }
        });

        Button backButton = menuButton("Volver");
        backButton.setOnAction(event -> showMainMenu());

        layout.getChildren().addAll(title, savedGames, loadButton, backButton);
        playBackgroundMusic("musica de fondo no partida.mp3");
        applyScene(new Scene(layout, 980, 640));
    }

    private void loadSavedGames(ListView<String> list) {
        list.getItems().clear();
        try {
            Files.list(Paths.get("."))
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .forEach(path -> list.getItems().add(path.getFileName().toString()));
        } catch (IOException ignored) {
            // Si no se puede listar, simplemente no se muestran partidas guardadas.
        }
    }

    private void showGame() {
        gameRoot = new BorderPane();
        gameRoot.setPadding(new Insets(18));
        gameRoot.setStyle(screenBackground("pantalla de dentro del juego.png"));
        gameRoot.setTop(buildGameHeader());
        gameRoot.setCenter(buildMapPanel());
        gameRoot.setRight(buildSidePanel());
        refresh();

        Scene scene = new Scene(gameRoot, 1360, 820);
        installGameHotkeys(scene);
        playGameBackground();
        applyScene(scene);
    }

    private HBox buildGameHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 10, 0));

        roomLabel = new Label();
        roomLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        turnLabel = new Label();
        turnLabel.setStyle("-fx-text-fill: #ffd166; -fx-font-size: 18px; -fx-font-weight: bold;");

        Button options = actionButton("Opciones", new Runnable() {
            public void run() {
                showGameOptionsPanel();
            }
        });
        options.setMinWidth(120);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(roomLabel, spacer, turnLabel, options);
        return header;
    }

    private VBox buildMapPanel() {
        VBox mapPanel = new VBox(0);
        mapPanel.setAlignment(Pos.CENTER);
        mapPanel.getChildren().add(buildMap());
        return mapPanel;
    }

    private void showOptionsMenu() {
        VBox options = new VBox(18);
        options.setAlignment(Pos.CENTER);
        options.setPadding(new Insets(40));
        options.setStyle(screenBackground("pantalla de cargar partida y de opciones del menu principal.png"));

        Label title = new Label("Opciones");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 34px; -fx-font-weight: bold;");

        Label description = new Label("Controles");
        description.setWrapText(true);
        description.setMaxWidth(520);
        description.setStyle("-fx-text-fill: #d8dee9; -fx-font-size: 18px;");

        Button back = menuButton("Volver");
        back.setOnAction(event -> showMainMenu());

        options.getChildren().addAll(title, description, buildVolumeControl(), buildControlsEditor(), back);
        playBackgroundMusic("musica de fondo no partida.mp3");
        applyScene(new Scene(options, 980, 640));
    }

    private void showGameOptionsPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(0, 8, 0, 14));
        panel.setPrefWidth(310);
        panel.setMinWidth(310);

        Button exitNoSave = actionButton("Salir sin guardar", new Runnable() {
            public void run() {
                showMainMenu();
            }
        });
        Button saveAndExit = actionButton("Guardar y salir", new Runnable() {
            public void run() {
                try {
                    new GameJsonRepository().save(engine.getState(), currentSavePath);
                    showMainMenu();
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            }
        });
        Button route = specialActionButton(finalRouteHintBought ? "Camino final comprado" : "Indicar camino final - 30 monedas", new Runnable() {
            public void run() {
                showShortestRouteHint();
            }
        });
        Button back = actionButton("Volver", new Runnable() {
            public void run() {
                gameRoot.setRight(buildSidePanel());
                refresh();
            }
        });

        panel.getChildren().addAll(sectionTitle("Opciones"), buildVolumeControl(), buildControlsEditor(), exitNoSave, saveAndExit, route, back);
        ScrollPane scroll = new ScrollPane(panel);
        scroll.setFitToWidth(true);
        scroll.setPrefWidth(330);
        scroll.setMinWidth(330);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        gameRoot.setRight(scroll);
    }

    private VBox buildVolumeControl() {
        Label title = sectionTitle("Sonido");
        Label value = new Label(Math.round(volumeLevel * 100) + "%");
        value.setStyle("-fx-text-fill: #fff4e6; -fx-font-size: 14px; -fx-font-weight: bold;");

        Slider slider = new Slider(0, 100, volumeLevel * 100);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(false);
        slider.setMajorTickUnit(25);
        slider.setBlockIncrement(5);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            volumeLevel = Math.max(0, Math.min(1, newValue.doubleValue() / 100.0));
            value.setText(Math.round(volumeLevel * 100) + "%");
            if (backgroundPlayer != null) {
                backgroundPlayer.setVolume(backgroundVolume());
            }
        });

        VBox control = new VBox(6, title, slider, value);
        control.setAlignment(Pos.CENTER_LEFT);
        control.setMaxWidth(520);
        control.setStyle("-fx-background-color: rgba(20, 8, 8, 0.72); -fx-padding: 12;"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #7f1111;"
                + " -fx-border-width: 2;");
        return control;
    }

    private void showShortestRouteHint() {
        if (finalRouteHintBought) {
            showError("Ya has comprado el indicador de camino");
            return;
        }
        if (!engine.getState().spendCoins(30)) {
            showError("Necesitas 30 monedas");
            return;
        }
        finalRouteHintBought = true;
        engine.getState().setFinalRouteHintBought(true);
        routeHintCells = calculateCurrentRoomRouteHint();
        engine.getState().getLog().add("Camino mas corto hacia la habitacion final marcado");
        refresh();
        showGameOptionsPanel();
    }

    private Lista<Position> calculateCurrentRoomRouteHint() {
        Lista<Position> empty = new Lista<Position>();
        Lista<String> path = engine.getState().getRoomGraph().shortestPath(
                engine.getState().getCurrentRoomId(), engine.getState().getFinalRoomId());
        if (path.size() < 2) {
            return empty;
        }
        Position targetDoor = doorToRoom(engine.getState().getRoom(), path.get(1));
        if (targetDoor == null) {
            return empty;
        }
        return cellPath(engine.getState().getPlayer().getPosition(), targetDoor);
    }

    private Position doorToRoom(Room room, String targetRoomId) {
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                Position position = new Position(row, column);
                Cell cell = room.getCell(position);
                if (cell.isDoor() && targetRoomId.equals(cell.getTargetRoomId())) {
                    return position;
                }
            }
        }
        return null;
    }

    private Lista<Position> cellPath(Position start, Position target) {
        Room room = engine.getState().getRoom();
        boolean[][] visited = new boolean[room.getRows()][room.getColumns()];
        Position[][] previous = new Position[room.getRows()][room.getColumns()];
        Cola<Position> queue = new Cola<Position>();
        queue.enqueue(start);
        visited[start.getRow()][start.getColumn()] = true;
        while (!queue.isEmpty()) {
            Position current = queue.dequeue();
            if (current.equals(target)) {
                break;
            }
            Direction[] directions = Direction.values();
            for (int i = 0; i < directions.length; i++) {
                Position next = current.translate(directions[i]);
                if (room.isValid(next) && !visited[next.getRow()][next.getColumn()]
                        && (next.equals(target) || room.getCell(next).isWalkable())) {
                    visited[next.getRow()][next.getColumn()] = true;
                    previous[next.getRow()][next.getColumn()] = current;
                    queue.enqueue(next);
                }
            }
        }
        if (!visited[target.getRow()][target.getColumn()]) {
            return new Lista<Position>();
        }
        Lista<Position> path = new Lista<Position>();
        Lista<Position> reverse = new Lista<Position>();
        Position current = target;
        while (current != null && !current.equals(start)) {
            reverse.add(current);
            current = previous[current.getRow()][current.getColumn()];
        }
        reverse.add(start);
        for (int i = reverse.size() - 1; i >= 0; i--) {
            path.add(reverse.get(i));
        }
        return path;
    }

    private VBox buildControlsEditor() {
        VBox controls = new VBox(5);
        controls.setMaxWidth(420);
        controls.getChildren().addAll(
                keyField("Mover arriba", moveUpKey, key -> moveUpKey = key),
                keyField("Mover izquierda", moveLeftKey, key -> moveLeftKey = key),
                keyField("Mover abajo", moveDownKey, key -> moveDownKey = key),
                keyField("Mover derecha", moveRightKey, key -> moveRightKey = key),
                keyField("Abrir puerta", openDoorKey, key -> openDoorKey = key),
                keyField("Recoger", pickUpKey, key -> pickUpKey = key),
                keyField("Pasar turno", passTurnKey, key -> passTurnKey = key),
                keyField("Inventario", inventoryKey, key -> inventoryKey = key),
                keyField("Atacar", attackKey, key -> attackKey = key),
                keyField("Apuntar arriba", aimUpKey, key -> aimUpKey = key),
                keyField("Apuntar izquierda", aimLeftKey, key -> aimLeftKey = key),
                keyField("Apuntar abajo", aimDownKey, key -> aimDownKey = key),
                keyField("Apuntar derecha", aimRightKey, key -> aimRightKey = key));
        return controls;
    }

    private HBox keyField(String labelText, KeyCode current, KeySetter setter) {
        Label label = statLabel();
        label.setText(labelText);
        label.setMinWidth(130);
        TextField field = new TextField(current.getName());
        field.setEditable(false);
        field.setFocusTraversable(true);
        field.setMaxWidth(120);
        field.setStyle("-fx-font-size: 13px; -fx-background-radius: 6;");
        field.setOnKeyPressed(event -> {
            setter.set(event.getCode());
            field.setText(event.getCode().getName());
            event.consume();
        });
        HBox row = new HBox(8, label, field);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void installGameHotkeys(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextField) {
                return;
            }
            if (engine == null || engine.getState().isFinished()) {
                return;
            }
            KeyCode code = event.getCode();
            if (code == moveUpKey) {
                moveWithDirection(Direction.UP);
            } else if (code == moveLeftKey) {
                moveWithDirection(Direction.LEFT);
            } else if (code == moveDownKey) {
                moveWithDirection(Direction.DOWN);
            } else if (code == moveRightKey) {
                moveWithDirection(Direction.RIGHT);
            } else if (code == openDoorKey) {
                doAction("abrir puerta", () -> engine.openDoor(selectedDirection));
            } else if (code == pickUpKey) {
                pickUpSelectedDirection();
            } else if (code == passTurnKey) {
                doAction("pasar turno", () -> engine.passTurn());
            } else if (code == inventoryKey) {
                showInventoryPanel();
            } else if (code == attackKey) {
                doAction("atacar", () -> engine.attack(selectedDirection));
            } else if (code == aimUpKey) {
                aim(Direction.UP);
            } else if (code == aimLeftKey) {
                aim(Direction.LEFT);
            } else if (code == aimDownKey) {
                aim(Direction.DOWN);
            } else if (code == aimRightKey) {
                aim(Direction.RIGHT);
            } else {
                return;
            }
            event.consume();
        });
    }

    private void moveWithDirection(Direction direction) {
        selectedDirection = direction;
        doAction("mover", () -> engine.move(direction));
    }

    private void aim(Direction direction) {
        selectedDirection = direction;
        refresh();
    }

    private interface KeySetter {
        void set(KeyCode key);
    }

    private GridPane buildMap() {
        mapGrid = new GridPane();
        mapGrid.setHgap(1);
        mapGrid.setVgap(1);
        mapGrid.setAlignment(Pos.CENTER);
        return mapGrid;
    }

    private VBox buildSidePanel() {
        VBox panel = new VBox(7);
        panel.setPadding(new Insets(0, 0, 0, 14));
        panel.setPrefWidth(310);
        panel.setMinWidth(310);

        playerTypeLabel = new Label();
        playerTypeLabel.setWrapText(true);
        playerTypeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        healthBar = new ProgressBar();
        healthBar.setMaxWidth(Double.MAX_VALUE);
        healthBar.setMinHeight(18);
        healthBar.setStyle("-fx-accent: #a73333;");

        healthTextLabel = new Label();
        healthTextLabel.setStyle("-fx-text-fill: #eef2f7; -fx-font-size: 13px; -fx-font-weight: bold;");

        attackLabel = statLabel();
        defenseLabel = statLabel();
        speedLabel = statLabel();
        coinsLabel = statLabel();
        HBox statsRow = new HBox(12, attackLabel, defenseLabel, speedLabel);
        statsRow.setAlignment(Pos.CENTER_LEFT);

        Button inventory = actionButton("Inventario", new Runnable() {
            public void run() {
                showInventoryPanel();
            }
        });
        selectedDirectionLabel = new Label();
        selectedDirectionLabel.setStyle("-fx-text-fill: #d8dee9; -fx-font-size: 15px; -fx-font-weight: bold;");

        HBox directionRow = buildDirectionPad();

        Button move = actionButton("Mover", new Runnable() {
            public void run() {
                doAction("mover", new GameRunnable() {
                    public void run() throws GameException {
                        engine.move(selectedDirection);
                    }
                });
            }
        });
        Button attack = actionButton("Atacar", new Runnable() {
            public void run() {
                doAction("atacar", new GameRunnable() {
                    public void run() throws GameException {
                        engine.attack(selectedDirection);
                    }
                });
            }
        });
        Button pick = actionButton("Recoger", new Runnable() {
            public void run() {
                pickUpSelectedDirection();
            }
        });
        Button door = actionButton("Abrir puerta", new Runnable() {
            public void run() {
                doAction("abrir puerta", new GameRunnable() {
                    public void run() throws GameException {
                        engine.openDoor(selectedDirection);
                    }
                });
            }
        });
        Button pass = actionButton("Pasar turno", new Runnable() {
            public void run() {
                doAction("pasar turno", new GameRunnable() {
                    public void run() throws GameException {
                        engine.passTurn();
                    }
                });
            }
        });

        logArea = buildLogPanel();
        logArea.setOnMouseClicked(event -> showLogPanel());
        VBox.setVgrow(logArea, Priority.ALWAYS);

        panel.getChildren().addAll(playerTypeLabel, healthTextLabel, healthBar, statsRow, coinsLabel,
                inventory, sectionTitle("Acciones"), selectedDirectionLabel, directionRow,
                attack, move, door, pick, pass, sectionTitle("Log"), logArea);
        return panel;
    }

    private boolean adjacentCellHasBlacksmith() {
        Position target = engine.getState().getPlayer().getPosition().translate(selectedDirection);
        return engine.getState().getRoom().isValid(target)
                && engine.getState().getRoom().getCell(target).hasBlacksmith();
    }

    private void pickUpSelectedDirection() {
        try {
            boolean blacksmith = adjacentCellHasBlacksmith();
            engine.pickUp(selectedDirection);
            if (blacksmith) {
                showForgePanel();
            } else {
        Lista<GameEngine.PickupEvent> events = engine.consumePickupEvents();
                for (GameEngine.PickupEvent event : events) {
                    if (event.getObject().getName().toLowerCase().contains("celestial")) {
                        playSound("Sonido de coger item celestial del suelo.mp3");
                    } else {
                        playSound("Sonido recoger item del suelo excepto celstiales.mp3");
                    }
                }
                refresh();
                animatePickupEvents(events);
            }
        } catch (GameException ex) {
            engine.getState().getLog().add("Error al intentar recoger: " + ex.getMessage());
            refresh();
            showError(ex.getMessage());
        }
    }

    private Label statLabel() {
        Label label = new Label();
        label.setStyle("-fx-text-fill: #eef2f7; -fx-font-size: 15px;");
        return label;
    }

    private HBox buildDirectionPad() {
        HBox directionRow = new HBox(6,
                directionButton("^", Direction.UP),
                directionButton("v", Direction.DOWN),
                directionButton("<", Direction.LEFT),
                directionButton(">", Direction.RIGHT));
        directionRow.setAlignment(Pos.CENTER);
        return directionRow;
    }

    private void showInventoryPanel() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(0, 0, 0, 18));
        panel.setPrefWidth(300);
        panel.setMinWidth(300);

        Label title = sectionTitle("Inventario");
        inventoryList = new ListView<String>();
        inventoryList.setPrefHeight(250);
        inventoryList.setStyle("-fx-control-inner-background: #202631; -fx-font-size: 14px;");
        inventoryList.setCellFactory(list -> new ListCell<String>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setGraphic(null);
                String border = isSelected() ? " -fx-border-color: white; -fx-border-width: 2;" : "";
                setStyle("-fx-background-color: #202631; -fx-text-fill: #eef2f7;" + border);
                if (!empty && getIndex() >= 0 && getIndex() < engine.getState().getPlayer().getInventory().size()) {
                    GameObject object = engine.getState().getPlayer().getInventory().get(getIndex());
                    ImageView icon = new ImageView(objectImage(object));
                    icon.setFitWidth(34);
                    icon.setFitHeight(34);
                    icon.setPreserveRatio(true);
                    StackPane square = new StackPane(icon);
                    square.setMinSize(42, 42);
                    square.setMaxSize(42, 42);
                    square.setStyle("-fx-background-color: #111827; -fx-border-color: #4b5563; -fx-border-width: 1;");
                    setGraphic(square);
                    if (object == engine.getState().getPlayer().getEquippedWeapon()) {
                        setStyle("-fx-background-color: #202631; -fx-text-fill: #eef2f7; -fx-border-color: #d62828; -fx-border-width: 2;" + (isSelected() ? " -fx-effect: dropshadow(gaussian, white, 4, 0, 0, 0);" : ""));
                    } else if (object == engine.getState().getPlayer().getEquippedArmor()) {
                        setStyle("-fx-background-color: #202631; -fx-text-fill: #eef2f7; -fx-border-color: #2a9d8f; -fx-border-width: 2;" + (isSelected() ? " -fx-effect: dropshadow(gaussian, white, 4, 0, 0, 0);" : ""));
                    }
                }
            }
        });

        inventoryDetailLabel = new Label("Selecciona un objeto");
        inventoryDetailLabel.setWrapText(true);
        inventoryDetailLabel.setStyle("-fx-text-fill: #d8dee9; -fx-font-size: 14px;");
        inventoryList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> updateInventoryDetail());

        Button use = actionButton("Usar seleccionado", new Runnable() {
            public void run() {
                final int index = inventoryList.getSelectionModel().getSelectedIndex();
                doAction("usar objeto", new GameRunnable() {
                    public void run() throws GameException {
                        engine.useInventoryItem(index);
                    }
                });
            }
        });

        Button equip = actionButton("Equipar seleccionado", new Runnable() {
            public void run() {
                int index = inventoryList.getSelectionModel().getSelectedIndex();
                if (index < 0 || index >= engine.getState().getPlayer().getInventory().size()) {
                    showError("Selecciona un objeto del inventario");
                    return;
                }
                doAction("equipar objeto", new GameRunnable() {
                    public void run() throws GameException {
                        engine.equipInventoryItem(index);
                    }
                });
            }
        });

        Button back = actionButton("Cerrar inventario", new Runnable() {
            public void run() {
                inventoryList = null;
                gameRoot.setRight(buildSidePanel());
                refresh();
            }
        });

        panel.getChildren().addAll(title, inventoryList, inventoryDetailLabel, use, equip, back);
        gameRoot.setRight(panel);
        refreshInventoryList();
    }

    private void showLogPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(0, 0, 0, 14));
        panel.setPrefWidth(310);
        panel.setMinWidth(310);

        TextArea fullLog = buildLogPanel();
        fullLog.setText(engine.getState().getLog().asText());
        VBox.setVgrow(fullLog, Priority.ALWAYS);
        Button back = actionButton("Cerrar log", new Runnable() {
            public void run() {
                gameRoot.setRight(buildSidePanel());
                refresh();
            }
        });
        panel.getChildren().addAll(sectionTitle("Log completo"), fullLog, back);
        gameRoot.setRight(panel);
    }

    private void showForgePanel() {
        mapGrid.setDisable(true);
        selectedForgeInventoryIndex = -1;
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(0, 0, 0, 14));
        panel.setPrefWidth(410);
        panel.setMinWidth(410);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab forgeTab = new Tab("Forjar", buildForgeTab());
        Tab shopTab = new Tab("Tienda", buildShopTab());
        tabs.getTabs().addAll(forgeTab, shopTab);
        VBox.setVgrow(tabs, Priority.ALWAYS);

        Button close = actionButton("Cerrar herrero", new Runnable() {
            public void run() {
                closeBlacksmithPanel();
            }
        });
        panel.getChildren().addAll(tabs, close);
        gameRoot.setRight(panel);
    }

    private HBox buildForgeTab() {
        HBox content = new HBox(10);
        VBox forgeBox = new VBox(8);
        forgeBox.setPrefWidth(190);
        StackPane slot = new StackPane();
        Label emptySlot = new Label("?");
        emptySlot.setStyle("-fx-text-fill: white; -fx-font-size: 30px;");
        slot.getChildren().add(emptySlot);
        slot.setAlignment(Pos.CENTER);
        slot.setMinSize(92, 92);
        slot.setMaxSize(160, 120);
        slot.setStyle("-fx-background-color: #202631; -fx-border-color: #8b99aa; -fx-border-width: 2; "
                + "-fx-padding: 8;");
        Label message = new Label("Nvl 1: armaduras +1 defensa, armas +1 ataque\n"
                + "Nvl 2: distancia +2 ataque, cuerpo a cuerpo +3 ataque, armaduras +2 defensa\n"
                + "Nvl 3: distancia +4 ataque, cuerpo a cuerpo +6 ataque, armaduras +4 defensa\n"
                + "Cada item solo se puede forjar una vez");
        message.setWrapText(true);
        message.setStyle("-fx-text-fill: #d8dee9; -fx-font-size: 13px;");

        Button forge1 = forgeButton("Forjar 5 monedas", "#6b7280", new Runnable() {
            public void run() {
                forgeSelected(1, 5);
            }
        });
        Button forge2 = forgeButton("Forjar 10 monedas", "#7c3aed", new Runnable() {
            public void run() {
                forgeSelected(2, 10);
            }
        });
        Button forge3 = forgeButton("Forjar 15 monedas", "linear-gradient(to right, #d62828, #f77f00)", new Runnable() {
            public void run() {
                forgeSelected(3, 15);
            }
        });
        forgeBox.getChildren().addAll(sectionTitle("Herrero"), slot, message, forge1, forge2, forge3);

        forgeInventoryIndexes = new Lista<Integer>();
        forgeInventoryList = new ListView<String>();
        forgeInventoryList.setPrefWidth(190);
        forgeInventoryList.setPrefHeight(350);
        forgeInventoryList.setStyle("-fx-control-inner-background: #202631; -fx-font-size: 14px;");
        forgeInventoryList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int selected = forgeInventoryList.getSelectionModel().getSelectedIndex();
            if (selected >= 0 && selected < forgeInventoryIndexes.size()) {
                selectedForgeInventoryIndex = forgeInventoryIndexes.get(selected);
                GameObject object = engine.getState().getPlayer().getInventory().get(selectedForgeInventoryIndex);
                updateForgeSlot(slot, object);
            }
        });
        refreshForgeInventoryList();

        VBox inventoryBox = new VBox(8, sectionTitle("Forjables"), forgeInventoryList);
        content.getChildren().addAll(forgeBox, inventoryBox);
        return content;
    }

    private void updateForgeSlot(StackPane slot, GameObject object) {
        slot.getChildren().clear();
        ImageView icon = new ImageView(objectImage(object));
        icon.setFitWidth(76);
        icon.setFitHeight(76);
        icon.setPreserveRatio(true);
        icon.setSmooth(true);
        Label name = new Label(object.getName());
        name.setWrapText(true);
        name.setMaxWidth(140);
        name.setAlignment(Pos.CENTER);
        name.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");
        VBox box = new VBox(4, icon, name);
        box.setAlignment(Pos.CENTER);
        slot.getChildren().add(box);
        slot.setStyle("-fx-background-color: #202631; -fx-border-color: #f4d35e; -fx-border-width: 2; "
                + "-fx-padding: 8;");
    }

    private VBox buildShopTab() {
        VBox shop = new VBox(10);
        shop.setPadding(new Insets(10, 0, 0, 0));

        Label coins = statLabel();
        coins.setText("Monedas: " + engine.getState().getCoins());

        ListView<String> sellList = new ListView<String>();
        sellList.setPrefHeight(160);
        sellList.setStyle("-fx-control-inner-background: #202631; -fx-font-size: 13px;");
        Lista<Integer> sellIndexes = new Lista<Integer>();
        refreshSellList(sellList, sellIndexes);
        Button sell = actionButton("Vender seleccionado", new Runnable() {
            public void run() {
                int selected = sellList.getSelectionModel().getSelectedIndex();
                if (selected < 0 || selected >= sellIndexes.size()) {
                    showError("Selecciona un objeto para vender");
                    return;
                }
                try {
                    engine.sellInventoryItem(sellIndexes.get(selected));
                    playSound("Sonido comprar item.mp3");
                    showForgePanel();
                    refresh();
                } catch (GameException ex) {
                    showError(ex.getMessage());
                }
            }
        });

        VBox buyBox = new VBox(6);
        buyBox.getChildren().add(sectionTitle("Comprar"));
        String[] ids = {"potion-health", "potion-max", "potion-defense", "potion-speed",
                "weapon-arco-celestial", "weapon-espada-celestial", "armor-celestial"};
        for (int i = 0; i < ids.length; i++) {
            final String itemId = ids[i];
            GameObject object = engine.shopObject(itemId);
            Button buy = actionButton(object.getName() + " - " + engine.buyPrice(itemId) + " monedas", new Runnable() {
                public void run() {
                    try {
                        engine.buyShopItem(itemId);
                        playSound("Sonido comprar item.mp3");
                        showForgePanel();
                        refresh();
                    } catch (GameException ex) {
                        showError(ex.getMessage());
                    }
                }
            });
            buyBox.getChildren().add(buy);
        }

        shop.getChildren().addAll(coins, sectionTitle("Vender"), sellList, sell, buyBox);
        return shop;
    }

    private void refreshSellList(ListView<String> sellList, Lista<Integer> sellIndexes) {
        sellList.getItems().clear();
        sellIndexes.clear();
        Lista<GameObject> inventory = engine.getState().getPlayer().getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            GameObject object = inventory.get(i);
            int price = engine.sellPrice(object);
            if (price > 0) {
                sellIndexes.add(i);
                sellList.getItems().add(object.getName() + " - " + price + " monedas");
            }
        }
    }

    private void closeBlacksmithPanel() {
        mapGrid.setDisable(false);
        forgeInventoryList = null;
        forgeInventoryIndexes = null;
        gameRoot.setRight(buildSidePanel());
        refresh();
    }

    private Button forgeButton(String text, String background, Runnable runnable) {
        Button button = actionButton(text, runnable);
        styleGameButton(button, 13, 6);
        return button;
    }

    private void refreshForgeInventoryList() {
        if (forgeInventoryList == null || forgeInventoryIndexes == null) {
            return;
        }
        forgeInventoryList.getItems().clear();
        forgeInventoryIndexes.clear();
        Lista<GameObject> inventory = engine.getState().getPlayer().getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            GameObject object = inventory.get(i);
            if (object.isForgeable()) {
                forgeInventoryIndexes.add(i);
                forgeInventoryList.getItems().add(object.getName() + " - " + object.bonusDescription());
            }
        }
    }

    private void forgeSelected(int level, int cost) {
        if (selectedForgeInventoryIndex < 0) {
            showError("Selecciona un objeto para forjar");
            return;
        }
        try {
            engine.forgeInventoryItem(selectedForgeInventoryIndex, level, cost);
            playSound("Golpe del herrero al mejorar un arma.mp3");
            showForgePanel();
            refresh();
        } catch (GameException ex) {
            showError(ex.getMessage());
        }
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 17px; -fx-font-weight: bold;");
        return label;
    }

    private TextArea buildLogPanel() {
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefRowCount(12);
        logArea.setStyle("-fx-control-inner-background: #202631; -fx-text-fill: #eef2f7; -fx-font-size: 13px;");
        return logArea;
    }

    private Button directionButton(String text, final Direction direction) {
        Button button = new Button(text);
        button.setMinHeight(38);
        styleGameButton(button, 14, 6);
        button.setOnAction(event -> {
            selectedDirection = direction;
            refresh();
        });
        return button;
    }

    private Button actionButton(String text, final Runnable runnable) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(40);
        styleGameButton(button, 14, 6);
        VBox.setVgrow(button, Priority.NEVER);
        button.setOnAction(event -> runnable.run());
        return button;
    }

    private Button specialActionButton(String text, final Runnable runnable) {
        Button button = actionButton(text, runnable);
        styleGameButton(button, 14, 6);
        return button;
    }

    private void doAction(String action, GameRunnable runnable) {
        try {
            String previousRoomId = engine.getState().getCurrentRoomId();
            int previousHealth = engine.getState().getPlayer().getHealth();
            int previousLogLines = logLineCount();
            runnable.run();
            playActionSounds(action, previousRoomId, previousHealth, previousLogLines);
            refresh();
            animateMovementEvents(engine.consumeMovementEvents());
            showCombatWarnings();
            if (engine.getState().isFinished()) {
                showEndScreen(engine.getState().isWon());
            }
        } catch (GameException ex) {
            engine.getState().getLog().add("Error al intentar " + action + ": " + ex.getMessage());
            refresh();
            showError(ex.getMessage());
        }
    }

    private void showEndScreen(boolean won) {
        StackPane endRoot = new StackPane();
        endRoot.setStyle(screenBackground(won ? "pantalla de victoria.png" : "pantalla de derrota.png"));
        endRoot.setOpacity(0);

        Button log = menuButton("Ver log");
        if (won) {
            styleSkyButton(log, 24, 8);
        }
        log.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Log de partida");
            alert.setHeaderText(won ? "Victoria" : "Derrota");
            TextArea finalLog = buildLogPanel();
            finalLog.setText(engine.getState().getLog().asText());
            finalLog.setPrefRowCount(18);
            finalLog.setPrefColumnCount(60);
            alert.getDialogPane().setContent(finalLog);
            alert.showAndWait();
        });

        Button exit = menuButton("Salir al menu");
        if (won) {
            styleSkyButton(exit, 24, 8);
        }
        exit.setOnAction(event -> showMainMenu());

        VBox buttons = new VBox(14, log, exit);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(40));
        buttons.setOpacity(0);
        endRoot.getChildren().add(buttons);

        playBackgroundMusic("musica de fondo no partida.mp3");
        if (!won) {
            playSound("Sonido cuando mueres.mp3");
        }
        applyScene(new Scene(endRoot, 980, 640));
        if (won) {
            FadeTransition backgroundFade = new FadeTransition(Duration.seconds(3), endRoot);
            backgroundFade.setFromValue(0);
            backgroundFade.setToValue(1);
            backgroundFade.play();

            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> {
                FadeTransition buttonsFade = new FadeTransition(Duration.millis(450), buttons);
                buttonsFade.setFromValue(0);
                buttonsFade.setToValue(1);
                buttonsFade.play();
            });
            delay.play();
        } else {
            endRoot.setOpacity(1);
            buttons.setOpacity(1);
        }
    }

    private void refresh() {
        GameState state = engine.getState();
        Room room = state.getRoom();
        finalRouteHintBought = state.isFinalRouteHintBought();
        if (finalRouteHintBought) {
            routeHintCells = calculateCurrentRoomRouteHint();
        }
        mapGrid.getChildren().clear();
        currentCellSize = cellSizeFor(room);
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                Position position = new Position(row, column);
                Button cellButton = new Button(symbolFor(position));
                cellButton.setMinSize(currentCellSize, currentCellSize);
                cellButton.setMaxSize(currentCellSize, currentCellSize);
                cellButton.setFocusTraversable(false);
                cellButton.setStyle(styleFor(room.getCell(position), position));
                Enemy enemy = room.getCell(position).getEnemy();
                if (enemy != null) {
                    cellButton.setOnAction(event -> showEnemyStats(enemy, position));
                } else if (room.getCell(position).hasBlacksmith()) {
                    cellButton.setOnAction(event -> showBlacksmithStats(position));
                }
                StackPane graphic = cellGraphic(position);
                if (graphic != null) {
                    cellButton.setText("");
                    cellButton.setGraphic(graphic);
                }
                mapGrid.add(cellButton, column, row);
            }
        }
        addFinalBossOverlay(room);
        int roomsToExit = state.getRoomGraph().minimumRooms(state.getCurrentRoomId(), state.getFinalRoomId());
        roomLabel.setText("Partida: " + currentSavePath
                + " / Jugador: " + state.getPlayer().getName()
                + " / Habitacion: " + state.getRoom().getName()
                + " / Habitaciones hasta salida: " + roomsToExit);
        turnLabel.setText("Turnos: " + state.getRemainingTurns());
        playerTypeLabel.setText(playerTypeText());
        int maxHealth = Math.max(1, state.getPlayer().getMaxHealth());
        healthBar.setProgress((double) state.getPlayer().getHealth() / maxHealth);
        healthBar.setTooltip(new Tooltip("Vida: " + state.getPlayer().getHealth() + "/" + maxHealth));
        healthTextLabel.setText("Vida: " + state.getPlayer().getHealth() + "/" + maxHealth);
        attackLabel.setText("Ataque: " + state.getPlayer().getAttackPower());
        if (state.getAviolentadoTurns() > 0) {
            defenseLabel.setText("Defensa: " + state.getPlayer().getDefensePower() + " -3 (aviolentado)");
        } else {
            defenseLabel.setText("Defensa: " + state.getPlayer().getDefensePower());
        }
        speedLabel.setText("Velocidad: " + state.getPlayer().getMovementPower());
        if (coinsLabel != null) {
            coinsLabel.setText("Monedas: " + state.getCoins());
        }
        selectedDirectionLabel.setText("Dir: " + selectedDirection + " | Movs: " + state.getMovementRemaining()
                + " | Accion: " + (state.isActionAvailable() ? "si" : "no"));

        refreshInventoryList();
        logArea.setText(state.getLog().asText());
        showEnemyHintIfNeeded();
        showBlacksmithHintIfNeeded();
        playGameBackground();
        playAmbientEnemySound(room);
    }

    private void animateMovementEvents(Lista<GameEngine.MovementEvent> events) {
        for (int i = 0; i < events.size(); i++) {
            GameEngine.MovementEvent event = events.get(i);
            Node node = mapNodeAt(event.getTo());
            if (node == null) {
                continue;
            }
            double fromX = (event.getFrom().getColumn() - event.getTo().getColumn()) * currentCellSize;
            double fromY = (event.getFrom().getRow() - event.getTo().getRow()) * currentCellSize;
            node.setTranslateX(fromX);
            node.setTranslateY(fromY);
            TranslateTransition transition = new TranslateTransition(Duration.millis(event.isPlayer() ? 160 : 240), node);
            transition.setToX(0);
            transition.setToY(0);
            transition.play();
        }
    }

    private void animatePickupEvents(Lista<GameEngine.PickupEvent> events) {
        for (int i = 0; i < events.size(); i++) {
            GameEngine.PickupEvent event = events.get(i);
            ImageView icon = entityImageView(objectImage(event.getObject()));
            StackPane overlay = new StackPane(icon);
            overlay.setMouseTransparent(true);
            overlay.setMinSize(currentCellSize, currentCellSize);
            overlay.setMaxSize(currentCellSize, currentCellSize);
            mapGrid.add(overlay, event.getFrom().getColumn(), event.getFrom().getRow());
            double toX = (event.getTo().getColumn() - event.getFrom().getColumn()) * currentCellSize;
            double toY = (event.getTo().getRow() - event.getFrom().getRow()) * currentCellSize;
            TranslateTransition transition = new TranslateTransition(Duration.millis(180), overlay);
            transition.setToX(toX);
            transition.setToY(toY);
            transition.setOnFinished(done -> mapGrid.getChildren().remove(overlay));
            transition.play();
        }
    }

    private Node mapNodeAt(Position position) {
        for (Node node : mapGrid.getChildren()) {
            Integer row = GridPane.getRowIndex(node);
            Integer column = GridPane.getColumnIndex(node);
            int safeRow = row == null ? 0 : row;
            int safeColumn = column == null ? 0 : column;
            if (safeRow == position.getRow() && safeColumn == position.getColumn()) {
                return node;
            }
        }
        return null;
    }

    private void showEnemyHintIfNeeded() {
        if (enemyHintShown || !roomHasEnemy(engine.getState().getRoom())) {
            return;
        }
        enemyHintShown = true;
        showInfoNotice("Consejo", "Click en el enemigo para ver estadisticas");
    }

    private boolean roomHasEnemy(Room room) {
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                if (room.getCell(new Position(row, column)).getEnemy() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private void showBlacksmithHintIfNeeded() {
        String roomId = engine.getState().getCurrentRoomId();
        if (blacksmithHintRooms.contains(roomId)) {
            return;
        }
        Room room = engine.getState().getRoom();
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                if (room.getCell(new Position(row, column)).hasBlacksmith()) {
                    blacksmithHintRooms.add(roomId);
                    showInfoNotice("Herrero", "Quiza te interesa hablar con el herrero (RECOGER para hablar con el herrero)");
                    return;
                }
            }
        }
    }

    private void refreshInventoryList() {
        if (inventoryList == null) {
            return;
        }
        inventoryList.getItems().clear();
        GameState state = engine.getState();
        Lista<GameObject> inventory = state.getPlayer().getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            inventoryList.getItems().add(inventory.get(i).getName());
        }
        updateInventoryDetail();
    }

    private void updateInventoryDetail() {
        if (inventoryDetailLabel == null || inventoryList == null) {
            return;
        }
        int index = inventoryList.getSelectionModel().getSelectedIndex();
        if (index < 0 || index >= engine.getState().getPlayer().getInventory().size()) {
            inventoryDetailLabel.setText("Selecciona un objeto");
            return;
        }
        GameObject object = engine.getState().getPlayer().getInventory().get(index);
        inventoryDetailLabel.setText(object.getName() + ": " + object.bonusDescription());
    }

    private int cellSizeFor(Room room) {
        int maxSide = Math.max(room.getRows(), room.getColumns());
        if (maxSide <= 3) {
            return 112;
        }
        if (maxSide <= 5) {
            return 90;
        }
        if (maxSide <= 8) {
            return 72;
        }
        if (maxSide <= 10) {
            return 60;
        }
        return 52;
    }

    private void showEnemyStats(Enemy enemy, Position position) {
        selectDirectionToward(position);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Enemigo");
        boolean boss = enemy.getId().equals("enemy-4-boss") || enemy.getId().equals("enemy-5-final-boss");
        String levelText = boss ? "Boss" : enemy.isSummoned() ? "Invocado" : "Nivel " + enemy.getLevel();
        if (enemy.getId().equals("enemy-5-final-boss") && enemy.isPhaseTwo()) {
            levelText += " - Violencia";
        }
        alert.setHeaderText(enemy.getName() + " - " + levelText + " (" + enemy.getDifficultyLabel() + ")");
        alert.setContentText("Tipo: Enemigo"
                + "\nCombate: " + enemy.getCombatType()
                + "\nDificultad: " + enemy.getDifficultyLabel()
                + "\nVida: " + enemy.getHealth()
                + "\nAtaque: " + enemy.getAttack()
                + "\nDefensa: 0"
                + "\nVelocidad: " + enemy.getSpeed()
                + "\nPosicion: " + position);
        alert.showAndWait();
        refresh();
    }

    private void selectDirectionToward(Position target) {
        Position player = engine.getState().getPlayer().getPosition();
        if (target.getRow() == player.getRow()) {
            selectedDirection = target.getColumn() > player.getColumn() ? Direction.RIGHT : Direction.LEFT;
        } else if (target.getColumn() == player.getColumn()) {
            selectedDirection = target.getRow() > player.getRow() ? Direction.DOWN : Direction.UP;
        }
    }

    private void showBlacksmithStats(Position position) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Herrero");
        alert.setHeaderText("herrero");
        alert.setContentText("Tipo: herrero"
                + "\nNo puedes hacerle dano"
                + "\nPosicion: " + position);
        alert.showAndWait();
    }

    private String playerTypeText() {
        String character = selectedCharacterClass().getDisplayName();
        String name = engine.getState().getPlayer().getName();
        if (selectedCharacterClass() == CharacterClass.NAPOLEON || "NAPOLEON".equals(name)) {
            return character + " / Easter egg: NAPOLEON";
        }
        return character;
    }

    private CharacterClass selectedCharacterClass() {
        return CHARACTERS[selectedCharacterIndex];
    }

    private Image characterImageForSelection() {
        if (selectedCharacterIndex >= 0 && selectedCharacterIndex < characterImages.length) {
            return characterImages[selectedCharacterIndex];
        }
        return napoleonImage;
    }

    private Image starterImage(CharacterClass characterClass) {
        if (characterClass == CharacterClass.ACORAZADO) {
            return starterShieldImage;
        }
        if (characterClass == CharacterClass.LADRON) {
            return starterDaggerImage;
        }
        if (characterClass == CharacterClass.OBISPO) {
            return starterCetroImage;
        }
        if (characterClass == CharacterClass.GUERRERO) {
            return starterSwordImage;
        }
        return starterTomahawkImage;
    }

    private StackPane cellGraphic(Position position) {
        Cell cell = engine.getState().getRoom().getCell(position);
        if (cell.isWall()) {
            return null;
        }
        StackPane stack = new StackPane();
        stack.setMinSize(currentCellSize - 2, currentCellSize - 2);
        stack.setMaxSize(currentCellSize - 2, currentCellSize - 2);
        stack.getChildren().add(tileImageView(floorImage));

        // Las trampas se camuflan con el suelo normal.
        if (cell.isDoor()) {
            stack.getChildren().add(entityImageView(doorImage(cell)));
        }
        if (cell.getObject() != null) {
            stack.getChildren().add(entityImageView(objectImage(cell.getObject())));
        }
        if (cell.getEnemy() != null) {
            if (!cell.getEnemy().getId().equals("enemy-5-final-boss")) {
                stack.getChildren().add(entityImageView(enemyImage(cell.getEnemy())));
            }
        }
        if (cell.hasBlacksmith()) {
            stack.getChildren().add(entityImageView(blacksmithImage));
        }
        if (engine.getState().getPlayer().getPosition().equals(position)) {
            Image currentPlayerImage = currentPlayerImage();
            if (currentPlayerImage != null) {
                stack.getChildren().add(entityImageView(currentPlayerImage));
            } else {
                Label player = new Label("J");
                player.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
                stack.getChildren().add(player);
            }
        }
        return stack;
    }

    private ImageView tileImageView(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(currentCellSize - 2);
        imageView.setFitHeight(currentCellSize - 2);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
        return imageView;
    }

    private ImageView entityImageView(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(currentCellSize - 6);
        imageView.setFitHeight(currentCellSize - 6);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setMouseTransparent(true);
        return imageView;
    }

    private void addFinalBossOverlay(Room room) {
        Enemy boss = null;
        Position anchor = null;
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                Position position = new Position(row, column);
                Enemy enemy = room.getCell(position).getEnemy();
                if (enemy != null && enemy.getId().equals("enemy-5-final-boss")) {
                    if (boss == null || row < anchor.getRow() || (row == anchor.getRow() && column < anchor.getColumn())) {
                        boss = enemy;
                        anchor = position;
                    }
                }
            }
        }
        if (boss == null || anchor == null) {
            return;
        }
        ImageView bossView = new ImageView(enemyImage(boss));
        bossView.setFitWidth(currentCellSize * 3.0 - 2);
        bossView.setFitHeight(currentCellSize * 3.0 - 2);
        bossView.setPreserveRatio(false);
        bossView.setSmooth(true);
        bossView.setMouseTransparent(true);
        StackPane overlay = new StackPane(bossView);
        overlay.setMouseTransparent(true);
        overlay.setMinSize(currentCellSize * 3.0, currentCellSize * 3.0);
        overlay.setMaxSize(currentCellSize * 3.0, currentCellSize * 3.0);
        mapGrid.add(overlay, anchor.getColumn(), anchor.getRow(), 3, 3);
    }

    private boolean isFinalBossAnchor(Enemy enemy, Position position) {
        Room room = engine.getState().getRoom();
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                Position candidate = new Position(row, column);
                if (room.getCell(candidate).getEnemy() == enemy) {
                    return candidate.equals(position);
                }
            }
        }
        return false;
    }

    private Image currentPlayerImage() {
        if (selectedCharacterClass() == CharacterClass.NAPOLEON || "NAPOLEON".equals(engine.getState().getPlayer().getName())) {
            return napoleonImage;
        }
        if (selectedCharacterIndex >= 0 && selectedCharacterIndex < characterImages.length) {
            return characterImages[selectedCharacterIndex];
        }
        return null;
    }

    private Image objectImage(GameObject object) {
        String id = object.getId().toLowerCase();
        String name = object.getName().toLowerCase();
        if (object.isCoin()) {
            return coinImage;
        }
        if (id.contains("llave-celestial")) {
            return potionImageFile("Llave_final.png");
        }
        if (id.contains("llave-demoniaca")) {
            return potionImageFile("Llave.png");
        }
        if (id.contains("starter-acorazado") || name.contains("escudo")) {
            return starterShieldImage;
        }
        if (id.contains("starter-ladron") || name.contains("daga")) {
            return starterDaggerImage;
        }
        if (id.contains("starter-obispo") || name.contains("cetro")) {
            return starterCetroImage;
        }
        if (id.contains("starter-guerrero") || name.contains("espada larga")) {
            return starterSwordImage;
        }
        if (id.contains("starter-vikingo") || name.contains("tomahawk")) {
            return starterTomahawkImage;
        }
        if (name.contains("arco celestial")) {
            return potionImageFile("Arco_Legendario.png");
        }
        if (name.contains("espada celestial")) {
            return potionImageFile("espada celestial df.png");
        }
        if (name.contains("arco de madera")) {
            return potionImageFile("Arco.png");
        }
        if (name.contains("hacha de guerra")) {
            return potionImageFile("Hacha.png");
        }
        if (name.contains("espada")) {
            return potionImageFile("Espada_normal.png");
        }
        if (name.contains("roja")) {
            return potionImageFile("Roja.jpg");
        }
        if (name.contains("tela")) {
            return potionImageFile("Basica.jpg");
        }
        if (name.contains("hermes")) {
            return potionImageFile("Hermes.jpg");
        }
        if (name.contains("hierro")) {
            return potionImageFile("Metal.jpg");
        }
        if (name.contains("celestial")) {
            return potionImageFile("Dios.jpg");
        }
        if (name.contains("velocidad")) {
            return potionImageFile("pocion_velocidad.jpg");
        }
        if (name.contains("defensa")) {
            return potionImageFile("Defensa.png");
        }
        if (name.contains("maxima") || name.contains("max")) {
            return potionImageFile("Pocion_Vida_grande.jpg");
        }
        if (object.getDamageBonus() != 0 || object.getDefenseBonus() > 0 || object.isWeapon() || object.isArmor()) {
            return weaponImage;
        }
        return potionImage;
    }

    private Image enemyImage(Enemy enemy) {
        String id = enemy.getId();
        if (id.contains("enemy-5") && enemy.isPhaseTwo()) {
            return marquettePhaseTwoImage;
        }
        if (id.contains("enemy-1")) {
            return enemyImages[0];
        }
        if (id.contains("enemy-2")) {
            return enemyImages[1];
        }
        if (id.contains("enemy-3")) {
            return enemyImages[2];
        }
        if (id.contains("enemy-4")) {
            return enemyImages[3];
        }
        if (id.contains("enemy-5")) {
            return enemyImages[4];
        }
        return enemyImages[0];
    }

    private Image doorImage(Cell cell) {
        if (cell.getTargetRoomId() == null && engine.getState().getCurrentRoomId().equals(engine.getState().getFinalRoomId())) {
            return cell.isOpen() ? doorFinalImage : finalDoorClosedImage;
        }
        if (cell.getRequiredKeyId() != null
                || "habitacion-24".equals(cell.getTargetRoomId())
                || "habitacion-24".equals(engine.getState().getCurrentRoomId())) {
            return cell.isOpen() ? lockedDoorOpenImage : lockedDoorClosedImage;
        }
        return cell.isOpen() ? doorInfernoImage : doorClosedImage;
    }

    private String symbolFor(Position position) {
        GameState state = engine.getState();
        if (state.getPlayer().getPosition().equals(position)) {
            return currentPlayerImage() == null ? "J" : "";
        }
        Cell cell = state.getRoom().getCell(position);
        if (cell.isWall()) {
            return "#";
        }
        if (cell.isDoor()) {
            return cell.isOpen() ? "/" : "D";
        }
        if (cell.getEnemy() != null) {
            return "E";
        }
        if (cell.hasBlacksmith()) {
            return "H";
        }
        if (cell.getObject() != null) {
            return "O";
        }
        if (cell.hasTrap()) {
            return "T";
        }
        return ".";
    }

    private String styleFor(Cell cell, Position position) {
        boolean routeHint = isRouteHint(position);
        String effectBorder = areaEffectBorder(position);
        if (engine.getState().getPlayer().getPosition().equals(position)) {
            return "-fx-background-color: #256d5b; -fx-text-fill: white; -fx-font-weight: bold; "
                    + "-fx-font-size: 18px; -fx-background-radius: 6; -fx-border-color: #d8f3dc; "
                    + "-fx-border-width: 2; -fx-border-radius: 6;";
        }
        String greenBorder = !effectBorder.isEmpty() ? effectBorder
                : routeHint ? "-fx-border-color: #22c55e; -fx-border-width: 3; -fx-border-radius: 2;" : "";
        if (cell.isWall()) {
            return "-fx-background-color: #2f323a; -fx-text-fill: white; -fx-font-size: 18px; "
                    + "-fx-background-radius: 2;" + greenBorder;
        }
        if (cell.getEnemy() != null) {
            return "-fx-background-color: #8f2d2d; -fx-text-fill: white; -fx-font-size: 18px; "
                    + "-fx-font-weight: bold; -fx-background-radius: 2;" + greenBorder;
        }
        if (cell.hasBlacksmith()) {
            return "-fx-background-color: #51412a; -fx-text-fill: #f4d35e; -fx-font-size: 18px; "
                    + "-fx-font-weight: bold; -fx-background-radius: 2;" + greenBorder;
        }
        if (cell.getObject() != null) {
            return "-fx-background-color: #b8842f; -fx-text-fill: white; -fx-font-size: 18px; "
                    + "-fx-font-weight: bold; -fx-background-radius: 2;" + greenBorder;
        }
        if (cell.isDoor()) {
            return "-fx-background-color: #5d4a8f; -fx-text-fill: white; -fx-font-size: 18px; "
                    + "-fx-font-weight: bold; -fx-background-radius: 2;" + greenBorder;
        }
        if (cell.hasTrap()) {
            return "-fx-background-color: #dfe5ec; -fx-text-fill: #222; -fx-font-size: 18px; "
                    + "-fx-font-weight: bold; -fx-background-radius: 2;" + greenBorder;
        }
        return "-fx-background-color: #dfe5ec; -fx-text-fill: #222; -fx-font-size: 18px; "
                + "-fx-background-radius: 2;" + greenBorder;
    }

    private boolean isRouteHint(Position position) {
        for (int i = 0; i < routeHintCells.size(); i++) {
            if (routeHintCells.get(i).equals(position)) {
                return true;
            }
        }
        return false;
    }

    private String areaEffectBorder(Position position) {
        for (GameState.AreaEffect effect : engine.getState().getAreaEffects()) {
            if (effect.contains(position)) {
                if (GameState.AreaEffect.RED.equals(effect.getColor())) {
                    return "-fx-border-color: #ef4444; -fx-border-width: 3; -fx-border-radius: 2;";
                }
                if (GameState.AreaEffect.YELLOW.equals(effect.getColor())) {
                    return "-fx-border-color: #facc15; -fx-border-width: 3; -fx-border-radius: 2;";
                }
                if (GameState.AreaEffect.GREEN.equals(effect.getColor())) {
                    return "-fx-border-color: #22c55e; -fx-border-width: 3; -fx-border-radius: 2;";
                }
                if (GameState.AreaEffect.WHITE.equals(effect.getColor())) {
                    return "-fx-border-color: #a855f7; -fx-border-width: 3; -fx-border-radius: 2;";
                }
            }
        }
        return "";
    }

    private void playGameBackground() {
        if (engine == null || engine.getState() == null) {
            playBackgroundMusic("musica de fondo no partida.mp3");
            return;
        }
        if (engine.getState().getCurrentRoomId().equals(engine.getState().getFinalRoomId())) {
            playBackgroundMusic("musica de fondo boss final.mp3");
        } else {
            playBackgroundMusic("musica de fondo partida.mp3");
        }
    }

    private void playBackgroundMusic(String fileName) {
        if (fileName.equals(currentBackgroundMusic) && backgroundPlayer != null) {
            return;
        }
        if (backgroundPlayer != null) {
            backgroundPlayer.stop();
            backgroundPlayer.dispose();
            backgroundPlayer = null;
        }
        Media media = soundMedia(fileName);
        if (media == null) {
            return;
        }
        try {
            backgroundPlayer = new MediaPlayer(media);
            backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundPlayer.setVolume(backgroundVolume());
            backgroundPlayer.play();
            currentBackgroundMusic = fileName;
        } catch (RuntimeException ignored) {
            currentBackgroundMusic = null;
        }
    }

    private void playSound(String fileName) {
        Media media = soundMedia(fileName);
        if (media == null) {
            return;
        }
        try {
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(effectVolume(fileName));
            player.setOnEndOfMedia(player::dispose);
            player.play();
        } catch (RuntimeException ignored) {
            // El juego sigue funcionando aunque JavaFX no pueda reproducir un MP3 concreto.
        }
    }

    private Media soundMedia(String fileName) {
        Path path = Paths.get("sonidos", fileName);
        if (!Files.exists(path)) {
            return null;
        }
        try {
            return new Media(path.toUri().toString());
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private double backgroundVolume() {
        return Math.max(0, Math.min(1, volumeLevel * 0.35));
    }

    private double effectVolume(String fileName) {
        double base = volumeLevel * 0.70;
        String lower = fileName.toLowerCase();
        if (lower.contains("musica")) {
            return backgroundVolume();
        }
        if (lower.contains("ametralladora") || lower.contains("disparo doble") || lower.contains("terremoto")) {
            return Math.min(1, base * 0.75);
        }
        if (lower.contains("rata") || lower.contains("duende") || lower.contains("dragon")) {
            return Math.min(1, base * 0.45);
        }
        return Math.min(1, base);
    }

    private void playAmbientEnemySound(Room room) {
        long now = System.currentTimeMillis();
        if (now - lastAmbientSoundMillis < 9000 || soundRandom.nextInt(100) >= 18) {
            return;
        }
        boolean rat = false;
        boolean goblin = false;
        boolean dragon = false;
        boolean machine = false;
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                Enemy enemy = room.getCell(new Position(row, column)).getEnemy();
                if (enemy == null) {
                    continue;
                }
                String id = enemy.getId();
                rat = rat || id.contains("enemy-1");
                goblin = goblin || id.contains("enemy-2");
                dragon = dragon || id.contains("enemy-3");
                machine = machine || id.contains("enemy-4");
            }
        }
        Lista<String> candidates = new Lista<String>();
        if (rat) {
            candidates.add(soundRandom.nextBoolean() ? "Sonido rata 1.mp3" : "Sonido rata 2.mp3");
        }
        if (goblin) {
            candidates.add("Sonido duende 1.mp3");
        }
        if (dragon) {
            candidates.add("Sonido dragon 1.mp3");
        }
        if (machine) {
            candidates.add(soundRandom.nextBoolean() ? "Sonido ametrallador 1.mp3" : "Sonido ametrallador 2.mp3");
        }
        if (!candidates.isEmpty()) {
            playSound(candidates.get(soundRandom.nextInt(candidates.size())));
            lastAmbientSoundMillis = now;
        }
    }

    private void playActionSounds(String action, String previousRoomId, int previousHealth, int previousLogLines) {
        if ("mover".equals(action)) {
            playSound("sonido pasos del personajes.mp3");
            if (!previousRoomId.equals(engine.getState().getCurrentRoomId())) {
                if (engine.getState().getCurrentRoomId().equals(engine.getState().getFinalRoomId())) {
                    playSound("Sonido al entrar a la puerta final.mp3");
                } else {
                    playSound("Sonido al entrar por una puerta.mp3");
                }
            }
        } else if ("usar objeto".equals(action)) {
            playSound("Sonido tomarse una pocion.mp3");
        } else if ("equipar objeto".equals(action)) {
            playSound("Equipar arma cuerpo a cuerpo excepto la celestial.mp3");
        } else if ("atacar".equals(action)) {
            playAttackSound();
        }
        playLogDrivenSounds(previousLogLines);
        if (engine.getState().getPlayer().getHealth() < previousHealth) {
            playSound("Sonido cuando te dan un ataque de habilidad de estas que tardan 1 tueno en aplicar.mp3");
        }
    }

    private void playAttackSound() {
        GameObject weapon = engine.getState().getPlayer().getEquippedWeapon();
        String name = weapon == null ? "" : weapon.getName().toLowerCase();
        if (name.contains("daga")) {
            playSound("Sonido de la daga.mp3");
        } else if (name.contains("escudo")) {
            playSound("sonido de ataque con el escudo.mp3");
        } else if (name.contains("cetro")) {
            playSound("Sonido de ataque del cetro del obispo.mp3");
        } else if (weapon != null && Enemy.RANGED.equals(weapon.getWeaponCombatType())) {
            playSound("Sonido arco del duende y arco de madera.mp3");
        } else {
            playSound("Sonido de arma cuerpo a cuerpo excepto la celestial.mp3");
        }
    }

    private void playLogDrivenSounds(int previousLogLines) {
        String[] lines = engine.getState().getLog().asText().split("\\R");
        for (int i = Math.max(0, previousLogLines); i < lines.length; i++) {
            String line = lines[i];
            if (line.contains("Disparo doble")) {
                playSound("Habilidad (disparo doble).mp3");
            } else if (line.contains("Flechazo perforante")) {
                playSound("Sonido habilidad (flecha perforante).mp3");
            } else if (line.contains("Invocacion")) {
                playSound("Sonido habilidad (invocacion goblin).mp3");
            } else if (line.contains("granadas")) {
                playSound("Sonido habilidad (granada) al ejecurtarse.mp3");
            } else if (line.contains("ametralladora")) {
                playSound("Habilidad (ametralladora).mp3");
            } else if (line.contains("quemado")) {
                playSound("Sonido habilidad (llamarada) al ejecutarse.mp3");
            } else if (line.contains("terremoto")) {
                playSound("Sonido habilidad (terremoto) al ejecutarse.mp3");
            } else if (line.contains("Huida") || line.contains("Teletransportacion")) {
                playSound("Sonido para habilidad (huida) y (deplazamiento lateral).mp3");
            } else if (line.contains("Ametrallador") && line.contains("ataca")) {
                playSound("Sonido ataque basico del ametrallador.mp3");
            } else if (line.contains("Marquette") && (line.contains("ataca") || line.contains("rayo"))) {
                playSound("Sonido ataque basico marquette.mp3");
            } else if (line.contains("duende") && line.contains("derrotado")) {
                playSound("Sonido de muerte duende arquero.mp3");
            }
        }
    }

    private int logLineCount() {
        if (engine == null || engine.getState() == null) {
            return 0;
        }
        String text = engine.getState().getLog().asText();
        if (text.isEmpty()) {
            return 0;
        }
        return text.split("\\R").length;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Accion no valida");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showCombatWarnings() {
        String log = engine.getState().getLog().asText();
        String[] lines = log.split("\\R");
        for (int i = Math.max(0, warningLogLineCursor); i < lines.length; i++) {
            String line = lines[i];
            if (isWarningLine(line)) {
                showWarningNotice(line);
            }
        }
        warningLogLineCursor = lines.length;
    }

    private boolean isWarningLine(String line) {
        return line.contains("Cuidado")
                || line.contains("Has pisado")
                || line.contains("Has sufrido")
                || line.contains("ejecutado")
                || line.contains("Juicio Final")
                || line.contains("Marquette esta enfadado")
                || line.contains("Aviolentacion")
                || line.contains("Disparo doble")
                || line.contains("Flechazo perforante")
                || line.contains("Invocacion")
                || line.contains("Solicitar ayuda")
                || line.contains("Teletransportacion")
                || line.contains("Curar:");
    }

    private void showInfoNotice(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarningNotice(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private interface GameRunnable {
        void run() throws GameException;
    }
}
