package NeuralNetwork;

import java.nio.file.LinkPermission;
import java.util.Arrays;
import java.util.List;
import NeuralNetwork.Utils;
import parser.Attribute;
import parser.Node;
import parser.Parser;
import parser.ParserTools;

public class NeuralNetwork {

    private double[][] output;
    private double[][][] weights;
    private double[][] bias;

    private double[][] error_signal;
    private double[][] output_derivative;

    public final int[] NETWORK_LAYER_SIZES;
    public final int INPUT_SIZE;
    public final int OUTPUT_SIZE;
    public final int NETWORK_SIZE;

    public NeuralNetwork(int... NETWORK_LAYER_SIZES){
        this.NETWORK_LAYER_SIZES = NETWORK_LAYER_SIZES;
        this.INPUT_SIZE = NETWORK_LAYER_SIZES[0];
        this.NETWORK_SIZE = NETWORK_LAYER_SIZES.length;
        this.OUTPUT_SIZE = NETWORK_LAYER_SIZES[NETWORK_SIZE-1];

        this.output = new double[NETWORK_SIZE][];
        this.weights = new double[NETWORK_SIZE][][];
        this.bias = new double[NETWORK_SIZE][];
        this.error_signal = new double[NETWORK_SIZE][];
        this.output_derivative = new double[NETWORK_SIZE][];

        for (int i=0; i<NETWORK_SIZE; i++){
            this.output[i] = new double[NETWORK_LAYER_SIZES[i]];
//            this.bias[i] = new double[NETWORK_LAYER_SIZES[i]];

            this.error_signal[i] = new double[NETWORK_LAYER_SIZES[i]];
            this.output_derivative[i] = new double[NETWORK_LAYER_SIZES[i]];

            this.bias[i] = Utils.createRandomArray(NETWORK_LAYER_SIZES[i], 0.3, 0.7);

            if (i>0) {
//                weights[i] = new double[NETWORK_LAYER_SIZES[i]][NETWORK_LAYER_SIZES[i - 1]];

                weights[i] = Utils.createRandomArray(NETWORK_LAYER_SIZES[i], NETWORK_LAYER_SIZES[i-1], -0.3, 0.5);
            }
        }
    }

    //FEED_FORWARD
    public double[] calculate(double... input){
        if (input.length != this.INPUT_SIZE) return null;
        this.output[0] = input;

        for (int layer=1; layer<NETWORK_SIZE; layer++){
            for (int neuron=0; neuron<NETWORK_LAYER_SIZES[layer]; neuron++){
                double sum = bias[layer][neuron];
                for (int prevNeuron=0; prevNeuron<NETWORK_LAYER_SIZES[layer-1]; prevNeuron++)
                    sum += output[layer-1][prevNeuron] * weights[layer][neuron][prevNeuron];

                output[layer][neuron] = sigmoid(sum);
                output_derivative[layer][neuron] = (output[layer][neuron] * (1 - output[layer][neuron]));
            }
        }

        return output[NETWORK_SIZE-1];
    }

    public void train(double[] input, double[] target, double eta){
        if (input.length != INPUT_SIZE || target.length != OUTPUT_SIZE) return;
        calculate(input);
        backpropError(target);
        updateWeights(eta);
    }

    public void backpropError(double[] target){
        for (int neuron=0; neuron<NETWORK_LAYER_SIZES[NETWORK_SIZE-1]; neuron++){
            error_signal[NETWORK_SIZE-1][neuron] =
//                    (output[NETWORK_SIZE-1][neuron] - target[neuron]) * output_derivative[NETWORK_SIZE-1][neuron];
                    (output[NETWORK_SIZE-1][neuron] - target[neuron]) * output_derivative[NETWORK_SIZE-1][neuron];
        }
        for (int layer=NETWORK_SIZE-2; layer>0; layer--){
            for (int neuron=0; neuron<NETWORK_LAYER_SIZES[layer]; neuron++){
                double sum = 0;
                for (int nextNeuron=0; nextNeuron<NETWORK_LAYER_SIZES[layer+1]; nextNeuron++){
                    sum += weights[layer +1][nextNeuron][neuron] * error_signal[layer+1][nextNeuron];
                }
                this.error_signal[layer][neuron] = sum * output_derivative[layer][neuron];
            }
        }
    }

    public void updateWeights(double eta){
        for (int layer=1; layer<NETWORK_SIZE; layer++){
            for (int neuron=0; neuron<NETWORK_LAYER_SIZES[layer]; neuron++){

                double delta = -eta * error_signal[layer][neuron];
                bias[layer][neuron] += delta;

                for (int prevNeuron=0; prevNeuron<NETWORK_LAYER_SIZES[layer-1]; prevNeuron++){
                    weights[layer][neuron][prevNeuron] += delta * output[layer-1][prevNeuron];
                }
            }
        }
    }

    private double sigmoid(double x){
        return 1d/(1 + Math.exp(-x));
    }

    public static void main(String[] args){
        NeuralNetwork net = new NeuralNetwork(7,10,7,4);
//
////        double[] in = new double[]{0.1, 0.2, 0.3, 0.4};
//        double[] in = new double[]{0.1, 0.2, 0.3, 0.4};
////        System.out.println(in.length);
////        System.exit(0);
//        double[] target = new double[]{0.9, 0.1};
//        double[] in2 = new double[]{0.6, 0.1, 0.4, 0.8};
//        double[] target2 = new double[]{0.1, 0.9};

        double[][] X = {
//                {0, 0},
//                {1, 0},
//                {0, 1},
//                {1, 1}
                {0.5,0,1,1,0,0,0},
                {0.5,0,0,1,0,0,0},
                {0.5,0.5,0,1,0,0,0},
                {0,0,1,1,0,0,0},
                {0.5,0,0,1,1,0,0},
                {0,0,0.5,0.5,1,0,0},
                {1,0.5,0,0,1,1,0},

                {0,0,0.5,1,1,0,0},
                {0,0,1,0,0,1,0},
                {0,1,1,0,0,0,0},
                {0,0,1,0,1,0,0},
                {0,0.5,0,1,1,0,0},
                {0,0.5,1,0,0,1,0},
                {0,0,1,0,0,0,0},

                {0,0,0.5,0.5,1,1,0},
                {0.5,0,0,1,1,0,0},
//                {0,0,1,0,1,0,0},
                {1,0.5,0,1,1,0,0},
                {0,0.5,0,1,1,0,0},
//                {0,0,1,0,0,1,0},
                {0,0,0.5,1,0,1,0},
                {0,0,0.5,0.5,0,0,0},
                {1,0,0.5,0.5,0,0,0},
                {0,0,1,0.5,0,0,0},
                {0,1,1,0.5,0,0,0},
                {1,0,1,0.5,0,0,0},
                {0,0,1,0.5,1,0,0},
                {0,0,1,0.5,0,1,0},
                {0,0,1,0.5,1,1,0},
                {0,0,1,0.5,0,0,1},
                {1,0,1,0.5,0,0,1},
                {0.5,0,0,0,0,0,0},
                {0.5,1,1,1,0,0,0},
                {0.5,0.5,1,1,0,0,0},
                {0.5,1,1,0.5,0,0,0},
                {0.5,0,1,0.5,0,0,0},
                {0.5,1,0,0.5,0,0,0},
                {0,0,0,0,0,0,0},
                {1,0,0,0,0,0,0},
                {1,0,0,0,1,0,0},
                {1,0,0,0,0,1,0},
                {1,0,0,0,1,1,0},
                {0,1,0,0,1,0,0},
                {0,1,0,0,0,1,0},
                {0,1,0,0,1,1,0},
//                {0,0,1,0,1,0,0},
//                {0,0,1,0,0,1,0},
                {0,0,1,0,1,1,0},
                {0,0,0,1,1,0,0},
                {0,0,0,1,0,1,0},
                {0,0,0,1,1,1,0},
                {1,1,0,0,1,0,0},
                {1,1,0,0,0,1,0},
                {1,1,0,0,1,1,0},
                {0,1,1,0,1,0,0},
                {0,1,1,0,0,1,0},
                {0,1,1,0,1,1,0},
                {0,0,1,1,1,0,0},
                {0,0,1,1,0,1,0},
                {0,0,1,1,1,1,0},
                {1,0,0,1,1,0,0},
                {1,0,0,1,0,1,0},
                {1,0,0,1,1,1,0},
                {0,1,0,1,1,0,0},
                {0,1,0,1,0,1,0},
                {0,1,0,1,1,1,0},
                {1,1,1,0,0,0,0},
                {1,0,1,1,0,0,0},
                {1,1,0,1,0,0,0},
                {1,0,0,0,0,0,1},
                {1,1,0,0,0,0,1},
                {1,1,1,0,0,0,1},
                {1,1,0,1,0,0,1},
                {0,1,1,1,0,0,1},
                {0,0,1,0,0,1,1},
                {1,1,0.5,1,0,0,0},
                {0,0,1,0.5,0,0,1},
//                {0,0,1,0,1,1,0},
                {0,0.5,0.5,1,0,0,0},
                {0,0.5,1,0,1,0,0},
                {1,0.5,1,0,0,0,1},
                {0,1,0,0,0,0,1},
                {0.5,0,1,0,0,0,1},
                {0,0,0,1,0,1,0},
                {0,0,0,1,0,0,0},
                {0,0.5,1,0,0,0,0},
                {0.5,0.5,1,0,0,0,1},
                {0.5,0.5,1,0,0,0,0},
                {1,0.5,0.5,0,0,0,0},
                {1,0,0.5,1,1,0,0}

        };
        double[][] Y = {
//            {0}, {1}, {2}, {0} //XOR
//                {0}, {1}, {1}, {0}, {2}
//                0 = up
//                1 = right
//                2 = down
//                3 = left
                {0,1,0,0},
                {0,0,1,0},
                {0,0,1,0},
                {1,0,0,0},
                {0,0,1,0},
                {1,0,0,0},
                {0,0,1,0},


                {1,0,0,0},
                {1,0,0,0},
                {0,0,0,1},
                {0,0,0,1},
                {1,0,0,0},
                {0,0,0,1},
                {0,0,0,1},

                {0,1,0,0},
                {0,1,0,0},
//                {0,1,0,0},
                {0,0,0,1},
                {1,0,0,0},
//                {0,1,0,0},
                {0,1,0,0},
                {1,0,0,0},
                {0,1,0,0},
                {1,0,0,0},
                {1,0,0,0},
                {0,1,0,0},
                {0,0,1,0},
                {0,0,1,0},
                {0,0,1,0},
                {1,0,0,0},
                {0,1,0,0},
                {0,1,0,0},
                {0,1,0,0},
                {0,0,1,0},
                {0,1,0,0},
                {0,1,0,0},
                {0,0,1,0},
                {1,0,0,0},
                {0,1,0,0},
                {1,0,0,0},
                {1,0,0,0},
                {1,0,0,0},
                {0,1,0,0},
                {0,1,0,0},
                {0,1,0,0},
//                {0,0,1,0},
//                {0,0,1,0},
                {0,1,0,0},
                {1,0,0,0},
                {0,0,0,1},
                {0,0,0,1},
                {0,0,1,0},
                {0,0,1,0},
                {0,0,1,0},
                {1,0,0,0},
                {1,0,0,0},
                {1,0,0,0},
                {0,1,0,0},
                {1,0,0,0},
                {1,0,0,0},
                {0,1,0,0},
                {1,0,0,0},
                {0,0,1,0},
                {0,1,0,0},
                {0,1,0,0},
                {0,1,0,0},
                {0,0,0,1},
                {0,1,0,0},
                {0,0,1,0},
                {0,1,0,0},
                {0,0,1,0},
                {0,0,0,1},
                {0,0,1,0},
                {1,0,0,0},
                {0,0,1,0},
                {1,0,0,0},
                {0,1,0,0},
//                {0,0,1,0},
                {1,0,0,0},
                {0,0,0,1},
                {0,0,0,1},
                {1,0,0,0},
                {0,0,0,1},
                {0,0,0,1},
                {0,1,0,0},
                {0,0,0,1},
                {0,0,0,1},
                {0,0,0,1},
                {0,0,1,0},
                {0,1,0,0}
//            {0}, {0}, {0}, {1} //AND
        };

        for (int epoch=0; epoch<10000; epoch++){
            for (int i=0; i<X.length; i++){
                net.train(X[i], Y[i], 0.3);
            }
        }

        //save the model
        try {
            net.saveNetwork("saved_model.txt");
        }catch (Exception e1){
            e1.printStackTrace();
        }

        int m = 0;
        for (double d[]: X){
//            int index = net.getIndexOfLargest(net.calculate(d));
            int index = Utils.indexOfHighestValue(net.calculate(d));
            m +=1;
            System.out.println("sample num: " + m + "\n"+ Arrays.toString(net.calculate(d)));
            System.out.println("max index: " + index);
            System.out.println("----------------------");
        }
//        System.out.println(Arrays.toString(net.calculate(in2)));
    }

    public void saveNetwork(String fileName) throws Exception {
        Parser p = new Parser();
        p.create(fileName);
        Node root = p.getContent();
        Node netw = new Node("Network");
        Node ly = new Node("Layers");
        netw.addAttribute(new Attribute("sizes", Arrays.toString(this.NETWORK_LAYER_SIZES)));
        netw.addChild(ly);
        root.addChild(netw);
        for (int layer = 1; layer < this.NETWORK_SIZE; layer++) {

            Node c = new Node("" + layer);
            ly.addChild(c);
            Node w = new Node("weights");
            Node b = new Node("biases");
            c.addChild(w);
            c.addChild(b);

            b.addAttribute("values", Arrays.toString(this.bias[layer]));

            for (int we = 0; we < this.weights[layer].length; we++) {

                w.addAttribute("" + we, Arrays.toString(weights[layer][we]));
            }
        }
        p.close();
    }

    public static NeuralNetwork loadNetwork(String fileName) throws Exception {

        Parser p = new Parser();

        p.load(fileName);
        String sizes = p.getValue(new String[] { "Network" }, "sizes");
        int[] si = ParserTools.parseIntArray(sizes);
        NeuralNetwork ne = new NeuralNetwork(si);

        for (int i = 1; i < ne.NETWORK_SIZE; i++) {
            String biases = p.getValue(new String[] { "Network", "Layers", new String(i + ""), "biases" }, "values");
            double[] bias = ParserTools.parseDoubleArray(biases);
            ne.bias[i] = bias;

            for(int n = 0; n < ne.NETWORK_LAYER_SIZES[i]; n++){

                String current = p.getValue(new String[] { "Network", "Layers", new String(i + ""), "weights" }, ""+n);
                double[] val = ParserTools.parseDoubleArray(current);

                ne.weights[i][n] = val;
            }
        }
        p.close();
        return ne;
    }
}