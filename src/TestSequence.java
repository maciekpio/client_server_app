import java.util.Random;

public class TestSequence {

    public static void main(String[] args){

        Random generator = new Random();
        long seed = generator.nextLong();
        int regex_complexity = 1;
        int sequence_length = 5;
        int request_variance = 5;
        String file_path = "C:\\SINF2MS\\LINGI2241\\Project\\client_server_app\\src\\dbdata.txt";

        System.out.println(1);
        Sequence seq1 = new Sequence(seed, regex_complexity, sequence_length, request_variance, file_path);

        System.out.println(2);
        seq1.save();

        System.out.println(3);

        Sequence seq2 = new Sequence(seed);

        System.out.println(seq1.equals(seq2));
    }

}
