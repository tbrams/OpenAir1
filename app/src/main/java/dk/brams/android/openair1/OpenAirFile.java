package dk.brams.android.openair1;



public class OpenAirFile {

    public OpenAirFile() {
    }

    // This is just a sample subset of what is available in the technical breakdown at soaringweb.org
    // Feel free to parse the files directly...
    private String[] mOpenAirCommands = {
            "* Reno CTR",
            "AC C",
            "AN RENO-C",
            "AL SFC",
            "AH 8400 ft",
            "V X=39:29.9 N 119:46.1 W",
            "DC 5",
            "",

            "* Reno TMA xx",
            "AC C",
            "V X=39:29.9 N 119:46.1W",
            "DA 10,270,290",
            "DA 7,290,320",
            "DA 10,320,200",
            "V D=-",
            "DA 5,200,270",
            "",


            "AC R",
            "AN R-4806 W",
            "AL 0",
            "AH UNLIM",
            "DP 36:41:00 N 115:56:10 W    c29",
            "DP 37:06:00 N 115:56:10 W    c30",
            "DP 37:06:00 N 115:35:00 W    c31",
            "DP 37:16:55 N 115:35:00 W    c32",
            "DP 37:16:55 N 115:18:10 W    c34",
            "DP 36:38:08 N 115:18:10 W    c35",
            "DP 36:25:40 N 115:18:10 W    c36",
            "DP 36:25:40 N 115:23:20 W    c37",
            "DP 36:35:00 N 115:37:00 W    c38",
            "DP 36:35:00 N 115:53:00 W    c39",
            "DP 36:35:45 N 115:56:10 W    c40",
            "",

            "AC D",
            "AN NAS-FALLON",
            "AL 0",
            "AH 6400ft",
            "V X=39:25.0 N 118:42.0 W",
            "DC 6",
            "",

            "AC D",
            "AN LAKE TAHOE",
            "AL 0",
            "AH 8800ft",
            "V X=38:53.6 N 119:59.7 W",
            "DC 5"
    };

    public String[] getCommands() {
        return mOpenAirCommands;
    }
}
