import java.util.ArrayList;
import java.util.Random;
import java.io.*;

public class Sequence {

    public String[] sequence;
    private long sequence_seed;     //a seed to generate the same sequence
    private int regex_complexity;   //the number of character in the regex, 0 < regex_complexity
    private int type_complexity;    //the number of type number in the request, 0 <= type_complexity <= 5
    private int sequence_length;    //the number of request in the sequence
    private int request_variance;   //the the number of different request in the sequence, if request_variance == sequence_length all the request are different

    public Sequence(long sequence_seed, int regex_complexity, int type_complexity, int sequence_length, int request_variance, String[] file) {

        this.sequence_seed = sequence_seed;
        this.regex_complexity = regex_complexity;
        this.type_complexity = type_complexity;
        this.sequence_length = sequence_length;
        this.request_variance = request_variance;

        this.sequence = generateSequence(file);
    }


    public Sequence(long sequence_seed) {

        try {

            String file_path =  "sequences/" + String.valueOf(sequence_seed) + ".txt";
            File file = new File(file_path);

            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));

                this.sequence_seed = sequence_seed;
                br.readLine();
                this.regex_complexity = Integer.valueOf(br.readLine());
                this.type_complexity = Integer.valueOf(br.readLine());
                this.sequence_length = Integer.parseInt(br.readLine());
                this.request_variance = Integer.parseInt(br.readLine());

                ArrayList<String> sequence_AL = new ArrayList<String>();
                String line;
                while ((line = br.readLine()) != null)
                    sequence_AL.add(line);
                br.close();
                String[] file_table = new String[sequence_AL.size()];
                this.sequence = sequence_AL.toArray(file_table);
            }

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }


    public void save() {
        String file_path = "sequences/" + String.valueOf(this.sequence_seed) + ".txt";
        File file = new File(file_path);

        if (file.exists()) return;

        try {
            file.createNewFile();
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(this.toString());
            myWriter.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public String toString() {

        String sequence_seed    = String.valueOf(this.sequence_seed);
        String regex_complexity = String.valueOf(this.regex_complexity);
        String type_complexity = String.valueOf(this.type_complexity);
        String sequence_length  = String.valueOf(this.sequence_length);
        String request_variance = String.valueOf(this.request_variance);
        String sequence         = String.join("\n", this.sequence);

        return  sequence_seed       + "\n" +
                regex_complexity    + "\n" +
                type_complexity     + "\n" +
                sequence_length     + "\n" +
                request_variance    + "\n" +
                sequence;
    }


    /**
     * @param file : the file dbdata.txt loaded in an array where each line are an entry
     * @return a sequence of request
     */
    public String[] generateSequence(String[] file) {

        String[] sequence = new String[this.sequence_length];
        String[] unique_request = new String[this.request_variance];

        Random generator = new Random(this.sequence_seed);
        for (int i = 0; i < this.request_variance; i++) {
            long request_seed = generator.nextLong();
            unique_request[i] = generateRequest(request_seed, file);
        }

        int[] random_indices_UR  = randomTable(this.sequence_seed, unique_request.length);
        int[] random_indices_SEQ = randomTable(this.sequence_seed+1, sequence.length);

        for (int i = 0; i < this.sequence_length; i++)
            sequence[random_indices_SEQ[i]] = unique_request[random_indices_UR[(i % unique_request.length)]];

        return sequence;
    }


    /**
     * @param file_path : the path to the file to store in the table
     * @return store each line of the file into a string
     */
    public static String[] loadFile(String file_path) {
        try {
            ArrayList<String> file_AL = new ArrayList<String>();

            File file = new File(file_path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null)
                file_AL.add(line);
            br.close();

            String[] file_table = new String[file_AL.size()];
            return file_AL.toArray(file_table);
        } catch (IOException ex) {
            System.out.println(ex);
            return null;
        }
    }


    /**
     * @param seed : a seed to generate the same request
     * @param file : the dbdata.txt file store in table where each line is a string
     * @return a request <type>;<regex>
     */
    public String generateRequest(long seed, String[] file) {

        String type;
        String regex;
        Random generator = new Random(seed);

        //find a line with a sentence long enough
        String line = "";
        boolean validLine = false;
        while (!validLine) {
            line = file[generator.nextInt(file.length)];
            if (line.split("@@@")[1].length() >= this.regex_complexity) validLine = true;
        }

        //generate a list of type
        if (this.type_complexity > 0) {
            int type_line = Integer.parseInt(line.split("@@@")[0]);
            type = line.split("@@@")[0];
            int[] random_table = randomTable(seed, 5);
            for (int i = 1, j = 0; i < this.type_complexity; i++, j++) {
                if (random_table[j] != type_line) type += "," + String.valueOf(random_table[j]);
                else {
                    j++;
                    type += "," + String.valueOf(random_table[j]);
                }
            }
        } else type = "";

        //generate a regex
        String sentence = line.split("@@@")[1];
        int r = generator.nextInt(sentence.length()-this.regex_complexity+1);
        regex =  sentence.substring(r, r+this.regex_complexity);

        return type + ";" + regex;
    }


    /**
     * @param seed : a seed to generate the same table
     * @param length : the length of the table
     * @return a table where the indices are randomized
     */
    private static int[] randomTable(long seed, int length) {
        Random generator = new Random(seed);
        int[] random_table = new int[length];
        int[] table_elem = new int[length];

        for (int i = 0; i < length; i++) table_elem[i] = i;

        for (int i = 0; i < length; i++) {
            boolean inTable = true;
            int r = 0;
            while (inTable) {
                r = generator.nextInt(length);
                if (table_elem[r] != -1) inTable = false;
            }
            random_table[i] = table_elem[r];
            table_elem[r] = -1;
        }
        return random_table;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Sequence) && ( this.toString().equals(obj.toString()) );
    }
}
