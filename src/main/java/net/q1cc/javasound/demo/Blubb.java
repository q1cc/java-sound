package net.q1cc.javasound.demo;

import javax.sound.sampled.*;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

public class Blubb {
    private static final int SAMPLE_COUNT = 200;
    private static boolean running = true;

    public static void run() throws LineUnavailableException {
        var audioFormat = new AudioFormat(
            /* encoding         */  PCM_SIGNED,
            /* sampleRate       */  44100,
            /* sampleSizeInBits */  16,
            /* channels         */  1,
            /* frameSize        */  2,
            /* frameRate        */  44100,
            /* bigEndian        */  false
        );
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();

        short[] sampleBuffer = new short[SAMPLE_COUNT];
        while (running) {
            fillSamples(sampleBuffer);
            ByteBuffer byteBuffer = ByteBuffer.allocate(SAMPLE_COUNT * 2);
            ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
            shortBuffer.put(sampleBuffer);
            var bbArray = byteBuffer.array();
            sourceDataLine.write(bbArray, 0, bbArray.length);
        }

        sourceDataLine.drain();
        sourceDataLine.close();
    }

    public static void fillSamples(short[] buffer) {
        var half = buffer.length / 2;
        for (int i = 0; i < half; i++) {
            buffer[i] = Short.MIN_VALUE;
        }
        for (int i = half; i < buffer.length; i++) {
            buffer[i] = Short.MAX_VALUE;
        }
    }
}
