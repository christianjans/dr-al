package com.cjkj.dral.bts;

import android.content.Context;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

import java.util.*;


public class CJDataManager {

    public enum CategoryInputType { NUMBERICAL, RADIO, DROPDOWN }

    private static String DATA_FOLDER = "";

    public static void updateFiles(Context context) {
        //final String SERVER_IP = "162.157.185.181";
        final String SERVER_IP = "127.0.0.1";
        //final String SERVER_IP = "10.0.0.6";
        final int PORT = 5005;

        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(SERVER_IP, PORT);
            Socket socket = new Socket();
            socket.connect(inetSocketAddress);

            File folder = new File(context.getFilesDir(), DATA_FOLDER);
            File[] listOfFiles = folder.listFiles();
            StringBuilder filenames = new StringBuilder();

            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        System.out.println("File " + file.getName() + " found in folder " + context.getFilesDir().getName() + DATA_FOLDER + ".");
                        filenames.append(file.getName());
                        filenames.append(" ");
                    }
                }
            }

            if (filenames.toString().equals("")) {
                sendServerMessage(socket, "NONE");
            } else {
                sendServerMessage(socket, filenames.toString());
            }

            String json = receiveServerMessage(socket);
            System.out.println("Done checking for updates...");

            handleJSONResponse(json, context);
            System.out.println("Done updating files...");

            socket.close();

        } catch (Exception e) {
            System.out.println("Got a socket error...");
            e.printStackTrace();

            if (e instanceof JSONException) {
                System.out.println("JSON error");
            }
        }
    }

    public static Map<String, Object> getDisplayData(Context context) {
        Map<String, Object> displayData = new HashMap<>();

        File folder = new File(context.getFilesDir(), DATA_FOLDER);
        File[] listOfFiles = folder.listFiles();

        String[] filenames = new String[listOfFiles.length];
        String[] titles = new String[listOfFiles.length];

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(".json")) {
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject fileObject = (JSONObject) parser.parse(new FileReader(listOfFiles[i]));

                    filenames[i] = (String) fileObject.get("filename");
                    titles[i] = (String) fileObject.get("title");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        displayData.put("filenames", filenames);
        displayData.put("titles", titles);

        return displayData;
    }

    public static Map<String, Object> getDiagnosingData(Context context, String filename) {
        Map<String, Object> diagnosingData = new HashMap<>();

        File file = new File(context.getFilesDir(), DATA_FOLDER + "/" + filename);

        try {
            JSONParser parser = new JSONParser();
            JSONObject fileObject = (JSONObject) parser.parse(new FileReader(file));

            String title = (String) fileObject.get("title");
            if (title != null) {
                diagnosingData.put("title", title);
            } else {
                diagnosingData.put("title", "");
            }

            // may have to multi-thread this

            String[] categories = getCategoriesFromJSON((JSONArray) fileObject.get("categories"));
            diagnosingData.put("categories", categories);

            CategoryInputType[] categoryInputTypes = getCategoryInputTypesFromJSON((JSONArray) fileObject.get("category_input_types"));
            diagnosingData.put("category_input_types", categoryInputTypes);

            String[][] categoryLabels = getCategoryLabelsFromJSON((JSONArray) fileObject.get("category_labels"));
            diagnosingData.put("category_labels", categoryLabels);

            double[][] categoryValues = getCategoryValuesFromJSON((JSONArray) fileObject.get("category_values"));
            diagnosingData.put("category_values", categoryValues);

            int[] netMap = getNetMapFromJSON((JSONArray) fileObject.get("net_map"));
            diagnosingData.put("net_map", netMap);

            ArrayList<double[][]> weights = getWeightsFromJSON((JSONArray) fileObject.get("weights"), netMap);
            diagnosingData.put("weights", weights);

            ArrayList<double[]> biases = getBiasesFromJSON((JSONArray) fileObject.get("biases"), netMap);
            diagnosingData.put("biases", biases);

            double[] categoriesMax = getCategoriesMaxFromJSON((JSONArray) fileObject.get("categories_max"));
            diagnosingData.put("categories_max", categoriesMax);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return diagnosingData;
    }

    public static double[][] getOthersData(Context context, String filename) {
        File file = new File(context.getFilesDir(), DATA_FOLDER + "/" + filename);

        try {
            JSONParser parser = new JSONParser();
            JSONObject fileObject = (JSONObject) parser.parse(new FileReader(file));

            JSONArray inputs = (JSONArray) fileObject.get("others");
            JSONArray results = (JSONArray) fileObject.get("others_results");

            return getOthersDataFromJSON((inputs == null) ? new JSONArray() : inputs, (results == null) ? new JSONArray() : results);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new double[0][0];
    }

    public static void saveAssetsAsFiles(Context context) {
        String[] filenames;

        try {
            filenames = context.getAssets().list(DATA_FOLDER);
        } catch(IOException e) {
            filenames = new String[0];
            e.printStackTrace();
        }

        for (String filename : filenames) {
            File f = new File(context.getFilesDir() + "/" + filename);
            if (f.getName().contains(".json") && !f.exists()) {
                try {
                    StringBuilder sb = new StringBuilder();
                    InputStream is = context.getAssets().open(filename);
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));

                    FileOutputStream fos = new FileOutputStream(f);

                    String line;
                    while ((line = br.readLine()) != null) {
                        fos.write(line.getBytes());
                    }

                    is.close();
                    fos.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static double[][] getOthersDataFromJSON(JSONArray inputs, JSONArray results) {
        JSONArray firstInputs = (JSONArray) inputs.get(0);
        int rows = inputs.size();
        int columns = firstInputs.size() + 1;
        double[][] othersData = new double[rows][columns];

        for (int i = 0; i < inputs.size(); i++) {
            JSONArray currentInputs = (JSONArray) inputs.get(i);
            JSONArray currentResults = (JSONArray) results.get(i);

            double result = ((double) currentResults.get(0) == 1) ? 1 : 0;

            for (int j = 0; j < currentInputs.size(); j++) {
                othersData[i][j] = (double) currentInputs.get(j);
            }

            othersData[i][currentInputs.size()] = result;
        }

        return othersData;
    }

    private static String[] getCategoriesFromJSON(JSONArray jsonCategories) {
        if (jsonCategories != null) {
            String[] categories = new String[jsonCategories.size()];

            for (int i = 0; i < jsonCategories.size(); i++) {
                categories[i] = (String) jsonCategories.get(i);
            }

            return categories;
        }

        return new String[0];
    }

    private static CategoryInputType[] getCategoryInputTypesFromJSON(JSONArray jsonCategoryInputTypes) {
        CategoryInputType[] categoryInputTypes = new CategoryInputType[jsonCategoryInputTypes.size()];

        for (int categoryIndex = 0; categoryIndex < jsonCategoryInputTypes.size(); categoryIndex++) {
            int categoryIntegerInputType = ((Long) jsonCategoryInputTypes.get(categoryIndex)).intValue();

            switch (categoryIntegerInputType) {
                case 0:
                    categoryInputTypes[categoryIndex] = CategoryInputType.NUMBERICAL;
                    break;
                case 1:
                    categoryInputTypes[categoryIndex] = CategoryInputType.RADIO;
                    break;
                case 2:
                    categoryInputTypes[categoryIndex] = CategoryInputType.DROPDOWN;
                    break;
            }
        }

        return categoryInputTypes;
    }

    private static String[][] getCategoryLabelsFromJSON(JSONArray jsonCategoryLabels) {
        String[][] categoryLabels = new String[jsonCategoryLabels.size()][];

        for (int categoryIndex = 0; categoryIndex < jsonCategoryLabels.size(); categoryIndex++) {
            JSONArray currentCategoryLabels = (JSONArray) jsonCategoryLabels.get(categoryIndex);
            String[] currentCategoryLabelsArray = new String[currentCategoryLabels.size()];

            for (int labelIndex = 0; labelIndex < currentCategoryLabels.size(); labelIndex++) {
                currentCategoryLabelsArray[labelIndex] = (String) currentCategoryLabels.get(labelIndex);
            }

            categoryLabels[categoryIndex] = currentCategoryLabelsArray;
        }

        return categoryLabels;
    }

    private static double[][] getCategoryValuesFromJSON(JSONArray jsonCategoryValues) {
        double[][] categoryValues = new double[jsonCategoryValues.size()][];

        for (int categoryIndex = 0; categoryIndex < jsonCategoryValues.size(); categoryIndex++) {
            JSONArray currentCategoryValues = (JSONArray) jsonCategoryValues.get(categoryIndex);
            double[] currentCategoryValuesArray = new double[currentCategoryValues.size()];

            for (int valueIndex = 0; valueIndex < currentCategoryValues.size(); valueIndex++) {
                currentCategoryValuesArray[valueIndex] = ((Long) currentCategoryValues.get(valueIndex)).doubleValue();
            }

            categoryValues[categoryIndex] = currentCategoryValuesArray;
        }

        return categoryValues;
    }

    private static ArrayList<double[][]> getWeightsFromJSON(JSONArray jsonWeights, int[] netMap) {
        if (jsonWeights != null) {
            ArrayList<double[][]> weights = new ArrayList<>();

            for (int i = 0; i < jsonWeights.size(); i++) {
                double[][] weightMatrix = new double[netMap[i + 1]][netMap[i]];
                JSONArray jsonWeightMatrix = (JSONArray) jsonWeights.get(i);

                for (int row = 0; row < netMap[i + 1]; row++) {
                    JSONArray jsonWeightMatrixRow = (JSONArray) jsonWeightMatrix.get(row);

                    for (int column = 0; column < netMap[i]; column++) {
                        weightMatrix[row][column] = (double) jsonWeightMatrixRow.get(column);
                    }
                }

                weights.add(weightMatrix);
            }

            return weights;
        }

        return null;
    }

    private static ArrayList<double[]> getBiasesFromJSON(JSONArray jsonBiases, int[] netMap) {
        ArrayList<double[]> biases = new ArrayList<>();

        for (int i = 0; i < jsonBiases.size(); i++) {
            double[] biasVector = new double[netMap[i + 1]];
            JSONArray jsonBiasVector = (JSONArray) jsonBiases.get(i);
            jsonBiasVector = (JSONArray) jsonBiasVector.get(0);

            for (int element = 0; element < biasVector.length; element++) {
                biasVector[element] = (double) jsonBiasVector.get(element);
            }

            biases.add(biasVector);
        }

        return biases;
    }

    private static double[] getCategoriesMaxFromJSON(JSONArray jsonCategoriesMax) {
        double[] categoriesMax = new double[jsonCategoriesMax.size()];

        for (int i = 0; i < categoriesMax.length; i++) {
            categoriesMax[i] = (double) jsonCategoriesMax.get(i);
        }

        return categoriesMax;
    }

    private static int[] getNetMapFromJSON(JSONArray jsonNetMap) {
        int[] netMap = new int[jsonNetMap.size()];

        for (int i = 0; i < netMap.length; i++) {
            netMap[i] = ((Long) jsonNetMap.get(i)).intValue();
        }

        return netMap;
    }

    private static void sendServerMessage(Socket socket, String message) throws Exception {
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        //dataOutputStream.writeBytes(message);
        //dataOutputStream.writeUTF(message);
        dataOutputStream.write(message.getBytes());
        dataOutputStream.flush();
    }

    private static String receiveServerMessage(Socket socket) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        final int MAX_BUFFER_SIZE = 1024;
        char[] buffer = new char[MAX_BUFFER_SIZE];
        int read;
        StringBuilder stringBuilder = new StringBuilder();

        read = bufferedReader.read(buffer, 0, buffer.length);

        while (read > 0) {
            stringBuilder.append(buffer, 0, read);
            //System.out.println(buffer);

            if (read < MAX_BUFFER_SIZE) {
                break;
            } else {
                read = bufferedReader.read(buffer, 0, buffer.length);
            }
        }

        bufferedReader.close();

        return stringBuilder.toString().trim();
    }

    private static void handleJSONResponse(String json, Context context) throws Exception {
        System.out.println(json);

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(json);
        JSONArray jsonDeleteArray = (JSONArray) jsonObject.get("delete");
        JSONArray jsonFilesArray = (JSONArray) jsonObject.get("files");

        int jsonDeleteArraySize = (jsonDeleteArray == null) ? 0 : jsonDeleteArray.size();

        for (int i = 0; i < jsonDeleteArraySize; i++) {
            String filename = (String) jsonDeleteArray.get(i);
            context.deleteFile(DATA_FOLDER + filename);
        }

        int jsonFilesArraySize = (jsonFilesArray == null) ? 0 : jsonFilesArray.size();

        for (int i = 0; i < jsonFilesArraySize; i++) {
            JSONObject fileObject = (JSONObject) jsonFilesArray.get(i);
            String filename = (String) fileObject.get("filename");

            FileOutputStream outputStream;

            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileObject.toString().getBytes());
            outputStream.close();
        }
    }

}
