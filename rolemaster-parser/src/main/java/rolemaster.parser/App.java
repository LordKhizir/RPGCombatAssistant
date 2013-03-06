package rolemaster.parser;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {

    private static final String CSV_DIR = "rolemaster-parser/src/main/resources/csv/";
    private static final String DIR_WEAPONS = CSV_DIR + "weapons";
    private static final String DIR_OTHER = CSV_DIR + "other";
    private static final String DIR_CRITICAL = CSV_DIR + "critical";
    private static final String DIR_OUTPUT = "rolemaster-parser/target/";
    private static final String ATTACKS_TYPES = DIR_OUTPUT + "attack_types";
    private static final String CRITICAL_TYPES = DIR_OUTPUT + "critical_types";
    private static final String OUTPUT_ATTACKS = DIR_OUTPUT + "attacks";
    private static final String OUTPUT_CRITICAL = DIR_OUTPUT + "critical";
    private static final int START_ID = 1;
    private static final String[] CRITICOS = { "K", "S", "P", "G", "U", "T" };
    private static final int[] CRITICOS_IDS = {  1,   2,   3,   4,   5,   6  };

    private static Pattern patternAttack;
    private static Pattern patternCritical;

    public static void main( String[] args ) throws IOException {
        File outDir = new File(DIR_OUTPUT);
        if (!outDir.exists()) {
            outDir.mkdir();
        }

        outDir = new File(OUTPUT_ATTACKS);
        if (!outDir.exists()) {
            outDir.mkdir();
        }

        outDir = new File(OUTPUT_CRITICAL);
        if (!outDir.exists()) {
            outDir.mkdir();
        }

        BufferedWriter outTypes = new BufferedWriter(new FileWriter(ATTACKS_TYPES));
        int idType = START_ID;

        File csvDir = new File(DIR_WEAPONS);
        File[] files = csvDir.listFiles();
        idType = processWeapons(outTypes, idType, files);

        csvDir = new File(DIR_OTHER);
        files = csvDir.listFiles();
        idType = processOther(outTypes, idType, files);

        outTypes.close();
        outTypes = new BufferedWriter(new FileWriter(CRITICAL_TYPES));
        csvDir = new File(DIR_CRITICAL);
        files = csvDir.listFiles();
        processCritical(outTypes, files);
        outTypes.close();
    }

    private static int processWeapons(BufferedWriter outTypes, int idType, File[] files) throws IOException {
        BufferedReader inAttack;
        String line;
        BufferedWriter outAttack;
        outTypes.write("#idType;attack_name;fumble;critical");
        outTypes.newLine();
        for (int i = 0 ; i < files.length ; i++) {
            outTypes.write(idType + ";" + parseFileName(files[i].getName()));
            outTypes.newLine();

            inAttack = new BufferedReader(new FileReader(files[i]));
            outAttack = new BufferedWriter(new FileWriter(OUTPUT_ATTACKS + "/" + files[i].getName()));
            int minimun = 150;
            int lineNumber = 0;
            while ((line = inAttack.readLine()) != null) {
                lineNumber++;
                if (line.equals("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0")) {
                    break;
                } else {
                    String lineParsed = readLine(line);
                    if (lineParsed == null) {
                        System.out.println("Error en fichero: " + files[i].getName() + " minimun/line: " + minimun + "/" + lineNumber);
                    } else {
                        outAttack.write(idType + ";" + minimun + ";" + lineParsed);
                        outAttack.newLine();
                    }
                }
                minimun--;
            }
            idType++;
            outAttack.close();
            inAttack.close();
        }
        return idType;
    }

    private static int processOther(BufferedWriter outTypes, int idType, File[] files) throws IOException {
        int[] minimums = new int[] {
                1, 3, 31, 34, 37, 40, 43, 46, 49, 52, 55, 58, 61, 64, 67, 70, 73, 76, 79, 82, 85, 88, 91, 94, 97, 100, 103,
                106, 109, 112, 115, 118, 121, 124, 127, 130, 133, 136, 139, 142, 145, 148
        };
        BufferedReader inAttack;
        String line;
        BufferedWriter outAttack;
        for (int i = 0 ; i < files.length ; i++) {
            outTypes.write(idType + ";" + parseFileName(files[i].getName()));
            outTypes.newLine();

            inAttack = new BufferedReader(new FileReader(files[i]));
            outAttack = new BufferedWriter(new FileWriter(OUTPUT_ATTACKS + "/" + files[i].getName()));
            int lineNumber = 0;
            while ((line = inAttack.readLine()) != null) {
                if (!line.equals("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0")) {
                    String lineParsed = readLine(line);
                    if (lineParsed == null) {
                        System.out.println("Error en fichero: " + files[i].getName() + " minimun/line: " + minimums[lineNumber] + "/" + lineNumber);
                    } else {
                        outAttack.write(idType + ";" + minimums[lineNumber] + ";" + lineParsed);
                        outAttack.newLine();
                    }
                }
                lineNumber++;
            }
            idType++;
            outAttack.close();
            inAttack.close();
        }
        return idType;
    }

    private static void processCritical(BufferedWriter outTypes, File[] files) throws IOException {
        int[] minimums = new int[] {
                1, 6, 11, 16, 21, 36, 46, 51, 56, 61, 66, 67, 71, 76, 81, 86, 91, 96, 100
        };
        BufferedReader inAttack;
        String line;
        BufferedWriter outAttack;
        for (int i = 0 ; i < files.length ; i++) {
            String[] array = files[i].getName().split("_");
            outTypes.write(array[1] + ";" + array[0].replaceAll("\\-", " "));
            outTypes.newLine();

            inAttack = new BufferedReader(new FileReader(files[i]));
            outAttack = new BufferedWriter(new FileWriter(OUTPUT_CRITICAL + "/" + files[i].getName()));
            int lineNumber = 0;
            while ((line = inAttack.readLine()) != null) {
                String lineParsed = readCriticalLine(line);
                if (lineParsed == null) {
                    System.out.println("Error en fichero: " + files[i].getName() + " minimun/line: " + minimums[lineNumber] + "/" + (lineNumber + 1));
                } else {
                    outAttack.write(array[1] + ";" + minimums[lineNumber] + ";" + lineParsed);
                    outAttack.newLine();
                }
                lineNumber++;
            }
            outAttack.close();
            inAttack.close();
        }
    }

    private static String readLine(String line) {
        String[] elems = line.split(",");
        boolean error = false;
        StringBuilder sb = new StringBuilder();
        if (elems.length == 20) {
            Matcher matcher;
            for (int i = 19 ; i >= 0 ; i--) {
                if (i < 19) {
                    sb.append(";");
                }
                matcher = getPatternAttack().matcher(elems[i]);
                if (matcher.matches()) {
                    sb.append(matcher.group(1));
                    if (matcher.group(2) != null) {
                        sb.append("-");
                        sb.append(matcher.group(2));
                        if (matcher.group(3) != null) {
                            sb.append("-");
                            int critical = parseCritical(matcher.group(3));
                            if (critical == 0) {
                                error = true;
                                System.out.println("Critico no válido: " + matcher.group(3));
                                break;
                            } else {
                                sb.append(critical);
                            }
                        }
                    }
                } else {
                    error = true;
                    System.out.println("No encaja: " + elems[i]);
                    break;
                }
            }
        } else {
            System.out.println("Error en nº de elementos: " + line);
            error = true;
        }

        if (error) {
            return null;
        } else {
            return sb.toString();
        }
    }

    private static String readCriticalLine(String line) {
        Matcher matcher = getPatternCritical().matcher(line);
        if (matcher.matches()) {
            return matcher.group(1) + ";" + matcher.group(2) + ";" + matcher.group(3) + ";" + matcher.group(4) + ";" +matcher.group(5);
        } else {
            return null;
        }
    }

    private static Pattern getPatternAttack() {
        if (patternAttack == null) {
            final String regex = "\"?(\\d\\d?) ?([A-F])?([KSPGUT])? *\"?";
            patternAttack = Pattern.compile(regex);
        }
        return patternAttack;
    }

    private static Pattern getPatternCritical() {
        if (patternCritical == null) {
            final String regex = "\"([^\"]+)\",\"([^\"]+)\",\"([^\"]+)\",\"([^\"]+)\",\"([^\"]+)\"";
            patternCritical = Pattern.compile(regex);
        }
        return patternCritical;
    }

    private static String parseFileName(String fileName) {
        String[] array = fileName.split("_");
        StringBuilder sb = new StringBuilder();
        sb.append(array[0].replaceAll("\\-", " "));
        sb.append(";");
        sb.append(array[1]);
        sb.append(";");
        sb.append(parseCritical(array[2]));
        return sb.toString();
    }

    public static int parseCritical(String critical) {
        int criticalInt = 0;
        for (int i = 0 ;  i < CRITICOS.length ; i++) {
            if (CRITICOS[i].equals(critical)) {
                criticalInt = CRITICOS_IDS[i];
                break;
            }
        }
        return criticalInt;
    }
}
