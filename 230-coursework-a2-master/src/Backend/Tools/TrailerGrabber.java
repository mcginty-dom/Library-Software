package Backend.Tools;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrailerGrabber {

    private static final String EMBED_TEMPLATE = "<iframe width=\"100%%\" height=\"95%%\" src=\"https://www.youtube.com/embed/%s?autoplay=1\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";
    private static final String LICENCING_ATTRIBUTION = "This product uses the TMDb API but is not endorsed or certified by TMDb.";

    private static final String KEY_LOCATION = "src/res/misc/TMDb_api";
    private static final String API_KEY;
    static {
        File file =
                new File(KEY_LOCATION);
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        API_KEY = sc.nextLine();
    }
    private static final String SEARCH_URL_TEMPLATE = "https://api.themoviedb.org/3/search/movie?api_key="+API_KEY+"&language=en-US&query=%s&page=1&include_adult=false";
    private static final String VIDEO_RETRIREVE_URL_TEMPLATE = "https://api.themoviedb.org/3/movie/%s/videos?api_key="+API_KEY+"&language=en-US";

    public static String getEmbeddedTrailer(String movieTitle){
        try {
            movieTitle = URLEncoder.encode(movieTitle, "utf-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Error encoding title for movie!");
            return null;
        }
        String movieID;
        try {
            movieID = searchMovieID(movieTitle);
        } catch (IOException | URISyntaxException e){
            System.out.println("Error searching for movie!");
            return null;
        }
        if(movieID == null){
            return null;
        }
        String embedCode;
        try {
            embedCode = getVideos(movieID);
        } catch (IOException | URISyntaxException e) {
            System.out.println("Error retrieving video information");
            return null;
        }
        if(embedCode == null){
            return null;
        }
        return createIFrame(embedCode);
    }


    private static String searchMovieID(String movieTitle) throws IOException, URISyntaxException {
        System.out.println(movieTitle);
        return extractID(requestURL(String.format(SEARCH_URL_TEMPLATE, movieTitle)));
    }

    private static String extractID(String json){
        /* Terrible hack to parse json, but the format allows it and should only fail when there is no available id per API specs */
        String idRegex = "\\\"id\\\":(.*?),";
        Pattern p = Pattern.compile(idRegex);
        Matcher m = p.matcher(json);
        m.find();
        try {
            return m.group(1);
        } catch (IllegalStateException e){
            System.out.println("No id in search result");
            return null;
        }
    }


    private static String getVideos(String movieID) throws IOException, URISyntaxException {
        return extractEmbedCode(requestURL(String.format(VIDEO_RETRIREVE_URL_TEMPLATE, movieID)));
    }


    private static String extractEmbedCode(String json){
        /* Terrible hack to parse json, but the format allows it and should only fail when there is no available video per API specs */
        String keyRegex = "\\\"key\\\":(.*?),";
        Pattern p = Pattern.compile(keyRegex);
        Matcher m = p.matcher(json);
        m.find();
        try {
            String code = m.group(1);
            return code.substring(1, code.length()-1);
        } catch (IllegalStateException e){
            System.out.println("No key in search result");
            return null;
        }
    }


    private static String createIFrame(String embedCode) {
        return String.format(EMBED_TEMPLATE, embedCode) + "\n" + LICENCING_ATTRIBUTION;
    }

    private static String requestURL(String url) throws IOException {
        URL searchURL = new URL(url);
        URLConnection yc = searchURL.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String inputLine = in.readLine();
        in.close();
        return inputLine;
    }

    public static void main(String... args){
        System.out.println(getEmbeddedTrailer("Harry Potter and the Philosopher's Stone"));
    }



}
