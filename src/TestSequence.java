import java.util.Random;

public class TestSequence {

    public static void main(String[] args){

        Random generator = new Random();
        long seed = generator.nextLong();
        int regex_complexity = 1;
        int sequence_length = 5;
        int request_variance = 5;
        String file_path = "dbdata.txt";

        Sequence seq1 = new Sequence(seed, regex_complexity, sequence_length, request_variance, file_path);
        seq1.save();

        Sequence seq2 = new Sequence(seed);

        System.out.println(seq1.equals(seq2));
    }

}
