package com.example.lab1;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.io.*;

public class HelloController {

    @FXML
    private MenuBar menuBar;
    @FXML
    private TabPane tabPane;
    @FXML
    private ImageView imageView;
    @FXML
    private Slider opacitySlider;
    @FXML
    private Slider hueSlider;
    @FXML
    private Slider satSlider;
    @FXML
    private Slider brightSlider;
    @FXML
    private ImageView greyChannel;
    @FXML
    private ImageView redChannel;
    @FXML
    private ImageView greenChannel;
    @FXML
    private ImageView blueChannel;
    @FXML
    private Label detailsLabel;
    @FXML
    private BarChart greyHistogram;
    @FXML
    private BarChart rgbHistogram;
    public static String filepath;
    public static Image originalImage;
    int[][] array;
    XYChart.Series<String, Number> seriesGrey = new XYChart.Series<>();
    XYChart.Series<String, Number> seriesRed = new XYChart.Series<>();
    XYChart.Series<String, Number> seriesGreen = new XYChart.Series<>();
    XYChart.Series<String, Number> seriesBlue = new XYChart.Series<>();

    public void initialize() {
        menuBar.getMenus().clear();
        Menu menuFile = new Menu("File");
        MenuItem openImage = new MenuItem("Open Image...");
        MenuItem exitApp = new MenuItem("Exit Application");
        openImage.setOnAction(actionEvent -> {
            tabPane.getSelectionModel().select(0);
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.webp"));
            File selectedFile = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
            if(selectedFile != null) {
                filepath = selectedFile.getAbsolutePath();
                try {
                    InputStream stream = new FileInputStream(filepath);
                    Image image = new Image(stream);
                    originalImage = image;
                    imageView.setImage(image);
                    convertToGreyscale();
                    convertToChannels();
                    populateHistogram();
                    loadDetails();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println("hiya :3");
        });
        exitApp.setOnAction(actionEvent -> {
            System.exit(0);
        });
        Menu menuImage = new Menu("Image");
        menuBar.getMenus().addAll(menuFile, menuImage);
        menuFile.getItems().addAll(openImage, exitApp);

        /*Menu menu = new Menu("test");
        MenuItem menuItem = new MenuItem("test");
        menuItem.setOnAction(actionEvent -> {
            System.out.println("hiya :3");
        });
        menuBar.getMenus().add(menu);
        menu.getItems().addAll(menuItem);
        System.out.println(menuBar.getMenus());*/
    }

    public void convertToGreyscale() {
        Image image = imageView.getImage();
        if(image != null) {
            PixelReader pixelReader = image.getPixelReader();
            WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
            PixelWriter pixelWriter = writableImage.getPixelWriter();
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    Color color = pixelReader.getColor(i, j);
                    color = color.grayscale();
                    pixelWriter.setColor(i, j, color);
                }
            }
            greyChannel.setImage(writableImage);
        }
    }

    public void convertToChannels() {
        Image image = imageView.getImage();
        if(image != null) {
            PixelReader pixelReader = image.getPixelReader();
            WritableImage redImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
            WritableImage greenImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
            WritableImage blueImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
            PixelWriter pixelWriterRed = redImage.getPixelWriter();
            PixelWriter pixelWriterGreen = greenImage.getPixelWriter();
            PixelWriter pixelWriterBlue = blueImage.getPixelWriter();
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    Color color = pixelReader.getColor(i, j);
                    Color red = new Color(color.getRed(), 0, 0, 1);
                    Color green = new Color(0, color.getGreen(), 0, 1);
                    Color blue = new Color(0, 0, color.getBlue(), 1);
                    pixelWriterRed.setColor(i, j, red);
                    pixelWriterGreen.setColor(i, j, green);
                    pixelWriterBlue.setColor(i, j, blue);
                }
            }
            redChannel.setImage(redImage);
            greenChannel.setImage(greenImage);
            blueChannel.setImage(blueImage);
        }
    }

    public void OnOpacitySliderDragged() {
        Image image = originalImage;
        if(image != null) {
            PixelReader pixelReader = image.getPixelReader();
            WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
            PixelWriter pixelWriter = writableImage.getPixelWriter();
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    Color color = pixelReader.getColor(i, j);
                    Color newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), opacitySlider.getValue());
                    pixelWriter.setColor(i, j, newColor);

                }
            }
            imageView.setImage(writableImage);
        }
    }

    /*public void OnHSBSliderDragged() {
        Image image = originalImage;
        if(image != null) {
            PixelReader pixelReader = image.getPixelReader();
            writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
            PixelWriter pixelWriter = writableImage.getPixelWriter();
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    Color color = pixelReader.getColor(i, j);
                    Color newColor = Color.hsb(hueSlider.getValue() * color.getHue(), satSlider.getValue(), brightSlider.getValue());
                    pixelWriter.setColor(i, j, newColor);
                }
            }
            imageView.setImage(writableImage);
        }
    }*/

    public void OnHSBSliderDragged() {
        Image image = originalImage;
        if(image != null) {
            /*if reverting back, set mins maxs and defaults to -1, 1, 0   :)
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(hueSlider.getValue());
            colorAdjust.setSaturation(satSlider.getValue());
            colorAdjust.setBrightness(brightSlider.getValue());
            imageView.setEffect(colorAdjust);*/
            PixelReader pixelReader = image.getPixelReader();
            WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
            PixelWriter pixelWriter = writableImage.getPixelWriter();
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    Color color = pixelReader.getColor(i, j);
                    Color newColor = color.deriveColor(hueSlider.getValue(), satSlider.getValue(), brightSlider.getValue(), 1);
                    pixelWriter.setColor(i, j, newColor);
                }
            }
            imageView.setImage(writableImage);
        }
    }

    public void populateHistogram() {
        System.out.println("Initializing arrays...");
        array = new int[256][4];
        Image image = originalImage;
        PixelReader pixelReader = image.getPixelReader();
        System.out.println("Calculating pixel data...");
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                Color color = pixelReader.getColor(i, j);
                int red = (int)(color.getRed()*255);
                int green = (int)(color.getGreen()*255);
                int blue = (int)(color.getBlue()*255);
                int greyColor = (int)(0.299*red + 0.587*green + 0.114*blue);
                //System.out.println(red + " " + green + " " + blue +" + greyColor);
                array[greyColor][0]++;
                array[red][1]++;
                array[green][2]++;
                array[blue][3]++;
            }
        }
        /*for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }*/
        System.out.println("Clearing series...");
        seriesGrey.getData().clear();
        seriesRed.getData().clear();
        seriesGreen.getData().clear();
        seriesBlue.getData().clear();
        System.out.println("Adding data...");
        for (int i = 0; i < array.length; i++) {
            String str = "" + i;
            seriesGrey.getData().add(new XYChart.Data<>(str, array[i][0]));
            seriesRed.getData().add(new XYChart.Data<>(str, array[i][1]));
            seriesGreen.getData().add(new XYChart.Data<>(str, array[i][2]));
            seriesBlue.getData().add(new XYChart.Data<>(str, array[i][3]));
        }
        System.out.println("Redirecting...");
        OnRadioSelected();
    }

    public void OnRadioSelected() {
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i][0] + ", " + array[i][1] + ", " + array[i][2] + ", " + array[i][3]);
        }
        System.out.println("Clearing histogram...");
        greyHistogram.getData().clear();
        rgbHistogram.getData().clear();
        System.out.println("Adding data...");

        System.out.println("Populating grey...");
        greyHistogram.getData().add(seriesGrey);
        for(Node n:greyHistogram.lookupAll(".default-color0.chart-bar")) {
            n.setStyle("-fx-bar-fill: grey;");
        }

        System.out.println("Populating red...");
        rgbHistogram.getData().add(seriesRed);
        System.out.println("Populating green...");
        rgbHistogram.getData().add(seriesGreen);
        System.out.println("Populating blue...");
        rgbHistogram.getData().add(seriesBlue);
        for(Node n:rgbHistogram.lookupAll(".default-color0.chart-bar")) {
            n.setStyle("-fx-bar-fill: red;");
        }
        for(Node n:rgbHistogram.lookupAll(".default-color1.chart-bar")) {
            n.setStyle("-fx-bar-fill: green;");
        }
        for(Node n:rgbHistogram.lookupAll(".default-color2.chart-bar")) {
            n.setStyle("-fx-bar-fill: blue;");
        }
        System.out.println("Done!");
    }

    public void loadDetails() {
        File file = new File(filepath);
        detailsLabel.setText("Filename:\n" +
                filepath + "\n\n" +
                "Dimensions:\n" +
                (int) originalImage.getWidth() + " x " + (int) originalImage.getHeight() + " pixels\n\n" +
                "Size:\n" +
                file.length() / 1024 + " KB");
    }

    public void OnSaveButton() throws IOException {
        /*FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        if(selectedFile.createNewFile()) {
            System.out.println("File Saved!");
        }*/
        /*DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(imageView.getScene().getWindow());
        if(selectedDirectory != null) {
            String str = selectedDirectory.getAbsolutePath();
            String newFilepath = str + "\\" + testText.getText() + ".png";
            File newFile = new File(newFilepath);
            if (newFile.createNewFile()) {
                System.out.println("File Saved! " + newFilepath);
            } else {
                System.out.println("File not saved :(");
            }
            ImageIO.write(SwingFXUtils.fromFXImage(imageView.getImage(), null), "png", newFile);*/
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png"));
        File selectedFile = fileChooser.showSaveDialog(imageView.getScene().getWindow());
        if(selectedFile != null) {
            ImageIO.write(SwingFXUtils.fromFXImage(imageView.getImage(), null), "png", selectedFile);
        }
    }

}