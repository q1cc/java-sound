package net.q1cc.javasound.demo;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.stream.Collectors;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

public class Blubb {
    private static final int SAMPLE_COUNT = 200;
    private static boolean running = true;

    public static void printAudioSystemInfo() {
        // Give some general info
        System.out.printf("%s\n-- General\n", "-".repeat(120));
        var mixerInfos = Arrays.stream(AudioSystem.getMixerInfo())
            .filter(Blubb::isViableMixer)
            .collect(Collectors.toList());
        System.out.printf("Audio system provides %d mixers of which %d are viable\n",
            AudioSystem.getMixerInfo().length,
            mixerInfos.size()
        );

        // Details
        System.out.printf("%s\n-- Details\n", "-".repeat(120));
        for (var i = 0; i < mixerInfos.size();  i++) {
            var mi = mixerInfos.get(i);
            System.out.printf("%02d: %s\n", i, mi);
            var mixer = AudioSystem.getMixer(mi);
            var mixerLines = Arrays.stream(mixer.getSourceLineInfo())
                .collect(Collectors.toList());
            for (var j = 0; j < mixerLines.size(); j++) {
                var mli = mixerLines.get(j);
                System.out.printf("\t%02d: %s\n", j, mli);
                if (mli instanceof DataLine.Info) {
                    var dli = (DataLine.Info) mli;
                    for (var af : dli.getFormats()) {
                        // Omit formats with less than 16 bits
                        if (af.getSampleSizeInBits() < 16) {
                            continue;
                        }
                        System.out.printf("\t\t%s\n", af);
                    }
                }
            }
//            mixer.isLineSupported();
        }
        System.out.printf("%s\n", "-".repeat(120));
    }

    public static boolean isViableMixer(Mixer.Info mixerInfo) {
        try {
            var mixer = AudioSystem.getMixer(mixerInfo);
            for (var lineInfo : mixer.getSourceLineInfo()) {
                if (SourceDataLine.class.isAssignableFrom(lineInfo.getLineClass())) {
                    return true;
                }
            }
            return false;
        } catch (Throwable e) {
            // Warn
            System.out.printf("WARNING: Exception while accessing mixer %s:\n", mixerInfo);
            e.printStackTrace();
        }
        return false;
    }

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
