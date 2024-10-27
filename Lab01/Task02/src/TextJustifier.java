import java.util.Arrays;

public class TextJustifier {
public static String[] justifyText(String[] words, int maxWidth) {
        String[] sentences = new String[words.length];
        int sentencesIndex = 0;
        int currentWordIndex = 0;

        while (currentWordIndex < words.length) {
            StringBuilder currentLine = new StringBuilder();
            int currentLength = words[currentWordIndex].length();
            int lastWordIndex = currentWordIndex + 1;

            while (lastWordIndex < words.length) {
                if (currentLength + 1 + words[lastWordIndex].length() > maxWidth) {
                    break;
                }

                currentLength += 1 + words[lastWordIndex].length();
                lastWordIndex++;
            }

            if (lastWordIndex == words.length || lastWordIndex - currentWordIndex == 1) {
                for (int i = currentWordIndex; i < lastWordIndex; i++) {
                    currentLine.append(words[i]);

                    if (i < lastWordIndex - 1) {
                        currentLine.append(" ");
                    }
                }

                while (currentLine.length() < maxWidth) {
                    currentLine.append(" ");
                }
            }

            else {
                int totalSpaces = maxWidth - currentLength + (lastWordIndex - currentWordIndex - 1);
                int gapsForSpaces = lastWordIndex - currentWordIndex - 1;

                if (gapsForSpaces > 0) {
                    int spacesCount = totalSpaces / gapsForSpaces;
                    int extraSpaces = totalSpaces % gapsForSpaces;

                    for (int i = currentWordIndex; i < lastWordIndex; i++) {
                        currentLine.append(words[i]);

                        if (i < lastWordIndex - 1) {
                            for (int j = 0; j < spacesCount; j++) {
                                currentLine.append(" ");
                            }

                            if (extraSpaces > 0) {
                                currentLine.append(" ");
                                extraSpaces--;
                            }
                        }
                    }
                }
            }

            sentences[sentencesIndex++] = currentLine.toString();
            currentWordIndex = lastWordIndex;
        }

        return Arrays.copyOf(sentences, sentencesIndex);
    }
}
