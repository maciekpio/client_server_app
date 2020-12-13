import java.util.Random;

public class TestSequence {

    public static void main(String[] args){

        Random generator = new Random();
        long seed = generator.nextLong();
        int regex_complexity = 30;
        int type_complexity = 4;
        int sequence_length = 20;
        int request_variance = 5;
        String file_path = "C:\\SINF2MS\\LINGI2241\\Project\\client_server_app\\src\\dbdata.txt";

        Sequence seq1 = new Sequence(seed, regex_complexity, type_complexity, sequence_length, request_variance, file_path);

        seq1.save();


        Sequence seq2 = new Sequence(seed);

        System.out.println(seq1.equals(seq2));
    }

}
