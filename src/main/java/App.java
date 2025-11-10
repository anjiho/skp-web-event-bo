import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    public static void main(String[] args) throws IOException {
        File file = new File("/Users/jihoan/Downloads/adreport.txt");
        if (file.exists()) {
            BufferedReader inFile = new BufferedReader(new FileReader(file));
            String sLine = null;
            String findText;
            //List<Float> listTotal = new ArrayList<>();
            float maxRunTime = 0, minRunTime = 100, allRunTime = 0;
            float maxLogRunTime = 0, minLogRunTime = 100, allLogRunTime = 0;
            int totalRunCount = 0, totalLogRunCount = 0;
            int maxtotalPoolSize = 0, maxactivePoolSize = 0, maxidlePoolSize = 0, maxwaitingPoolSize = 0;

            while ((sLine = inFile.readLine()) != null) {
                if (sLine.indexOf("> Total Run Time") >= 0) {
                    findText = findString(".+Total Run Time   : (.*)(sec)", sLine, 1);
                    if (findText != null) {
                        float findTime = Float.parseFloat(findText);
                        totalRunCount++;

                        if (findTime > maxRunTime) {
                            maxRunTime = findTime;
                        } else if (findTime < minRunTime) {
                            minRunTime = findTime;
                        }
                        allRunTime += findTime;
                    }
                } else if (sLine.indexOf("Pool stats ") >= 0) {
                    findText = findString("Pool stats.*total=(.*?)\\,", sLine, 1);
                    if (Integer.parseInt(findText) > maxtotalPoolSize) {
                        maxtotalPoolSize = Integer.parseInt(findText);
                    }

                    findText = findString("Pool stats.*active=(.*?)\\,", sLine, 1);
                    if (Integer.parseInt(findText) > maxactivePoolSize) {
                        maxactivePoolSize = Integer.parseInt(findText);
                    }

                    findText = findString("Pool stats.*idle=(.*?)\\,", sLine, 1);
                    if (Integer.parseInt(findText) > maxidlePoolSize) {
                        maxidlePoolSize = Integer.parseInt(findText);
                    }

                    findText = findString("Pool stats.*waiting=(.*?)\\)", sLine, 1);
                    if (Integer.parseInt(findText) > maxwaitingPoolSize) {
                        maxwaitingPoolSize = Integer.parseInt(findText);
                    }
                } else if (sLine.indexOf("= TOTAL RUN TIME :") >= 0) {
                    findText = findString("= TOTAL RUN TIME : (.*)(sec)", sLine, 1);
                    if (findText != null) {
                        float findTime = Float.parseFloat(findText);
                        totalLogRunCount++;

                        if (findTime > maxRunTime) {
                            maxLogRunTime = findTime;
                        } else if (findTime < minLogRunTime) {
                            minLogRunTime = findTime;
                        }
                        allLogRunTime += findTime;
                    }
                }
            }
            inFile.close();

            System.out.println(String.format("Pool stats 최고치      : total=%d, active=%d, idle=%d, waiting=%d", maxtotalPoolSize, maxactivePoolSize, maxidlePoolSize, maxwaitingPoolSize));

            System.out.println("총 요청개수           : " + totalRunCount);
            System.out.println("최저 수행시간          : " + minRunTime);
            System.out.println("최고 수행시간          : " + maxRunTime);
            System.out.println("평균 수행시간          : " + (allRunTime / totalRunCount));

            if (totalLogRunCount == 0) {
                minLogRunTime = 0;
            }

            System.out.println("로그서비스 콜 최저 수행시간 : " + minLogRunTime);
            System.out.println("로그서비스 콜 최고 수행시간 : " + maxLogRunTime);
            System.out.println("로그서비스 콜 평균 수행시간 : " + (allLogRunTime / totalLogRunCount));
        }
    }

    private static String findString(String strPattern, String strMatcher, int findGroupIndex) {
        String ret = null;
        Pattern pattern = Pattern.compile(strPattern);
        Matcher matcher = pattern.matcher(strMatcher);

        String find = null;
        while (matcher.find()) {
            find = matcher.group(findGroupIndex);

            if (find != null) {
                ret = find;
            }
            if (find == null) break;
        }

        return ret;
    }

}