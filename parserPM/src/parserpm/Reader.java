/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parserpm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import javafx.scene.chart.LineChart;

/**
 *
 * @author sbt-steshenko-ma
 */
// Класс, отвечающий за парсинг данных и построение графика
public class Reader {

    private LineChart<String, Number> lineChart;
    final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
    String start;
    String end;
    String strUrl;
    String path;
    String graphName;
    boolean check = false;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");

    Reader(String start, String end, String strUrl, String path, String graphName) {

        this.start = start;
        this.end = end;
        this.strUrl = strUrl;
        this.path = path;
        this.graphName = graphName;

        parseURL(); // парсим данные
        buildChart(); // строим график
        System.out.println(graphName + ": DONE");

    }

    ;

    //Метод, который считывает данные с url из файла urls.properties
    //HP Performance Manager возвращается данные только за 8 часов 25 минут
    //Поэтому считывание данных осуществляется в 2 режима:
    //(1) Когда время (от и до) меньше 8 часов 25 минут
    //(2) Когда время (от и до) больше 8 часов 25 минут
public void parseURL() {

        Date startDate = new TimeParser(start).getDate();
        Date endDate = new TimeParser(end).getDate();

        long diff = getDateDiff(startDate, endDate, TimeUnit.MINUTES);

        //(1) Когда время (от и до) меньше 8 часов 25 минут
        if (diff <= 495) {

            String newURL = strUrl;
            newURL = newURL.replaceAll("start", start);
            newURL = newURL.replaceAll("end", end);

            try {

                URL url = new URL(newURL);
                HttpURLConnection a = (HttpURLConnection) url.openConnection();
                a.setRequestMethod("GET");
                a.setRequestProperty("User-Agent", "Mozilla/5.0");
                a.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
                BufferedReader in = new BufferedReader(new InputStreamReader(a.getInputStream()));

                writeFile(in);

                System.out.println(graphName + ": " + start + " - " + end);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //(2) Когда время (от и до) больше 8 часов 25 минут
        // Считываем данные равными отрезками по 495 минут
        if (diff > 495) {

            boolean readFirstLine = false; //Условие для считывания первой строки с заголовками

            while (diff != 0) {

                if (diff > 495) {

                    long endtm = startDate.getTime() + 495 * ONE_MINUTE_IN_MILLIS;

                    Date e = new Date(endtm);
                    String endNew = sdf.format(e);

                    String newURL = strUrl;
                    newURL = newURL.replaceAll("start", start);
                    newURL = newURL.replaceAll("end", endNew);

                    try {

                        URL url = new URL(newURL);

                        HttpURLConnection a = (HttpURLConnection) url.openConnection();
                        a.setRequestMethod("GET");
                        a.setRequestProperty("User-Agent", "Mozilla/5.0");
                        a.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
                        BufferedReader in = new BufferedReader(new InputStreamReader(a.getInputStream()));

                        if (readFirstLine == true) {
                            in.readLine();
                            in.readLine();
                        }
                        writeFile(in);
                        System.out.println(graphName + ": " + start + " - " + endNew);
                    } catch (IOException ed) {
                        System.err.println("Ошибка в парсинге данных" + ed);
                    }

                    diff = diff - 495;

                    start = endNew;
                    startDate = e;
                    readFirstLine = true;

                }
                //Cчитываем остаток, который меньше 495 минут
                if (diff < 495 || diff == 495) {

                    long endtm = startDate.getTime() + diff * ONE_MINUTE_IN_MILLIS;
                    Date e = new Date(endtm);

                    String endNew = sdf.format(e);

                    String newURL = strUrl;
                    newURL = newURL.replaceAll("start", start);
                    newURL = newURL.replaceAll("end", endNew);

                    try {

                        URL url = new URL(newURL);

                        HttpURLConnection a = (HttpURLConnection) url.openConnection();
                        a.setRequestMethod("GET");
                        a.setRequestProperty("User-Agent", "Mozilla/5.0");
                        a.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
                        BufferedReader in = new BufferedReader(new InputStreamReader(a.getInputStream()));
                        in.readLine();
                        in.readLine();
                        writeFile(in);

                        System.out.println(graphName + ": " + start + " - " + endNew);
                    } catch (IOException ed) {
                        System.err.println("Ошибка в парсинге данных" + ed);
                    }

                    diff = diff - diff;

                }

            }

        }

    }

    //Метод, возвращающий разницу между начальной и конечной датами в минутах
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    //Метод, строящий график
    public void buildChart() {

        try {

            FileReader fw = new FileReader(path + "\\" + graphName + ".csv");

            CategoryAxis xAxis = new CategoryAxis();
            //xAxis.setLabel("Время"); 
            xAxis.setTickLabelRotation(360);
            NumberAxis yAxis = new NumberAxis();
            //yAxis.setLabel("%"); 
            lineChart = new LineChart<String, Number>(xAxis, yAxis);
            lineChart.setTitle(graphName);

            String inputLine;
            String arr[];

            BufferedReader in = new BufferedReader(fw);

            inputLine = in.readLine();
            inputLine = inputLine.replace("<pre>", "");

            arr = inputLine.split(";"); //Поделили первую строку, в которой первый столбец время
            ArrayList<XYChart.Series> seriesList = new ArrayList();
            for (int i = 1; i < arr.length; i++) {
                XYChart.Series series = new XYChart.Series();
                series.setName(arr[i]);
                seriesList.add(series);
            }
            String date = null;
            while ((inputLine = in.readLine()) != null) {
                inputLine = inputLine.replace("<pre>", "");
                if (inputLine.equals("")) {
                    continue;
                }
                arr = inputLine.split(";");
                date = arr[0];
                
                for (int i = 0; i < seriesList.size(); i++) {
                    
                    double y = 0;
                    XYChart.Series series = seriesList.get(i);
                    try {
                        if (!arr[i + 1].contains("-")) {
                            y = Double.parseDouble(arr[i + 1].replace(",", "."));
                        }
                        series.getData().add(new XYChart.Data(date, y));
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        System.err.println("Проблема со входным файлом, некоторые значения пропущены. Все столбцы должны быть полностью заполнены." + inputLine);
                    }
                }
            }
            for (int i = 0; i < seriesList.size(); i++) {
                lineChart.getData().add(seriesList.get(i));
            }

            lineChart.setAnimated(false);
            lineChart.getStylesheets().add("grafanaStyle.css");

            
         //   Set<Node> nodes = lineChart.lookupAll(".series0"); // series0 is the style class for first series 
         //   for (Node n : nodes) { n.setStyle("-fx-background-color: blue;"); }
            
            writeImageToDir();

        } catch (IOException e) {
            System.err.println(e);
        }

    }

    //Метод, записывающий график в директорию в формате png
    public void writeImageToDir() {
        File fileImage = new File(path + "\\" + graphName + ".png");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Scene scene = new Scene(lineChart, 700, 500);
                WritableImage chart = lineChart.snapshot(new SnapshotParameters(), null);
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(chart, null), "png", fileImage);
                } catch (IOException ex) {
                    System.err.println("LineChart: " + lineChart.getId() + ": Непредвиденная ошибка при построении графика. (ImageIO)");

                }
            }
        });

    }

    //Метод, записывающий данные в директорию в формате csv
    public void writeFile(BufferedReader in) {

        try (FileWriter fw = new FileWriter(path + "\\" + graphName + ".csv", true)) {
            String inputLine;

            while ((inputLine = in.readLine()) != null) {

                if (inputLine.contains(":")) {

                    String part1 = "";
                    String part2 = "";
                    String sumLine = "";

                    part1 = inputLine.substring(0, 8);
                    part2 = inputLine.substring(8);
                    part2 = part2.replace(",", ";");
                    part2 = part2.replace(".", ",");

                    sumLine = part1 + part2;

                    sumLine = part1 + part2;
                    sumLine = sumLine.replace("<pre>", "");
                    fw.write(sumLine + "\n");

                } else {

                    inputLine = inputLine.replace("<pre>", "");
                    inputLine = inputLine.replace(",", ";");
                    if (!check) {
                        fw.write(inputLine + "\n");
                        if (!check) {
                            check = true;
                        }
                    }

                }

            }
            fw.close();

        } catch (IOException exx) {
            System.err.println("Ошибка в записи данных на диск" + exx);
        }

    }

}
