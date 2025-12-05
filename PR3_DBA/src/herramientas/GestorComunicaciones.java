package herramientas;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GestorComunicaciones {

    public static Boolean isCorrectMensajeAgente(String msg) {
        return (msg.startsWith("Bro") && msg.endsWith("En plan."));
    }

    public static Boolean isCorrectMensajeSantaClaus(String msg) {
        return (msg.startsWith("Joulupukki") && msg.endsWith("Kiitos.")) || (msg.startsWith("Hyvää joulua") && msg.endsWith("Nähdään pian."));
    }

    // Generación del string si es digno o no
    public static String SantaClausConfirmaDigno(Boolean confirma, String idConv) {
        String msg;

        if (confirma) {
            msg = "Hyvää joulua, Eres un crack valiente. ID [" + idConv + "]. Nähdään pian.";
        } else {
            msg = "Hyvää joulua, Estamos acabados. Nähdään pian.";
        }

        return msg;
    }

    public static String traduceAgente_SantaClaus(String msg) {
        String finalMsg = msg;

        if (finalMsg.startsWith("Bro")) {
            finalMsg = finalMsg.replaceFirst("Bro", "Joulupukki");
        }

        if (finalMsg.endsWith("En plan.")) {
            finalMsg = finalMsg.replaceFirst("En plan\\.$", "Kiitos."); // Asegura el reemplazo correcto del final.
        }

        return finalMsg;
    }

    public static String traduceSantaClaus_Agente(String msg) {
        String finalMsg = msg;

        if (finalMsg.startsWith("Hyvää joulua")) {
            finalMsg = finalMsg.replaceFirst("Hyvää joulua", "Bro");
        }

        if (finalMsg.endsWith("Nähdään pian.")) {
            finalMsg = finalMsg.replaceFirst("Nähdään pian\\.?\\s*$", "En plan.");
        }

        return finalMsg;
    }

    public static String obtenerTotem(String msg) {
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(msg);
        matcher.find();
        return matcher.group(1);
    }
}
