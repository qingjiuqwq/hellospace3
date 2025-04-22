/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space;

import org.lwjgl.glfw.GLFW;
import space.hack.Hack;
import space.hack.hud.Hud;
import space.hack.hud.element.MusicLyrics;
import space.manager.EventsHandler;
import space.manager.HackManager;
import space.utils.Utils;
import space.value.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class Core {

    public static boolean mode;

    // Get
    public static String DisposalGet(final SeverMode severMode) {
        switch (severMode.httpPath) {
            case "/hello" -> {
                return "Hello-Space";
            }
            case "/list" -> {
                StringBuilder list = new StringBuilder();
                String first = severMode.is2("MusicLyricsFirst");
                if (first != null) {
                    MusicLyrics.first = Utils.base64(first);
                }
                String second = severMode.is2("MusicLyricsSecond");
                if (second != null) {
                    MusicLyrics.second = Utils.base64(second);
                }
                for (final Hack h : HackManager.getHack()) {
                    list.append("[#(").append(h.getName()).append("[#&&#]").append("[").append(h.isToggled())
                            .append("]").append(h.isCategory()).append(")#][#@@#]");
                }
                return list.toString();
            }
            case "/config", "/visual" -> {
                return getConfig(1, severMode.isPath("/visual")) + getConfig(2, severMode.isPath("/visual"));
            }
        }
        return "https://npyyds.top/";
    }

    public static String getConfig(int array, boolean visual) {
        StringBuilder list = new StringBuilder();

        boolean isHack;
        ArrayList<Hack> hack = new ArrayList<>();
        ArrayList<Hud> hud = new ArrayList<>();
        ArrayList<HaCd> haCds = new ArrayList<>();

        if (array == 1) {
            isHack = true;
            hack = HackManager.getHack();
            haCds.addAll(hack);
        } else {
            isHack = false;
            hud = HackManager.getHud();
            haCds.addAll(hud);
        }

        for (int i = 0; i < haCds.size(); i++) {
            HaCd h = haCds.get(i);
            StringBuilder text = new StringBuilder();
            text.append("---").append(h.getName()).append("---\n");

            if (isHack && hack.get(i).isToggled() || !isHack && hud.get(i).isToggled()) {
                if (visual) {
                    text.append("Open-true\n");
                } else {
                    text.append("[#(Open[#&&#]true)#][#@@#]");
                }
            }

            String key = h.getKeyString();
            if (!key.equals("NONE")) {
                if (visual) {
                    text.append("Key-").append(key).append("\n");
                } else {
                    text.append("[#(Key[#&&#]").append(key).append(")#][#@@#]");
                }
            }

            if (!isHack && hud.get(i).x != 0) {
                if (visual) {
                    text.append("PosX-").append(hud.get(i).x).append("\n");
                } else {
                    text.append("[#(PosX[#&&#]").append(hud.get(i).x).append(")#][#@@#]");
                }
            }

            if (!isHack && hud.get(i).y != 0) {
                if (visual) {
                    text.append("PosY-").append(hud.get(i).y).append("\n");
                } else {
                    text.append("[#(PosY[#&&#]").append(hud.get(i).y).append(")#][#@@#]");
                }
            }

            for (final Value<?> value : h.getValues()) {
                if (!HackManager.isDefault(value)) {
                    text.append(visual ? "" : "[#(").append(value.getName()).append(visual ? "-" : "[#&&#]")
                            .append(value instanceof ModeValue hh ? hh.getMode() : value.getValue())
                            .append(visual ? "\n" : ")#][#@@#]");
                }
            }

            if (!text.toString().equals("---" + h.getName() + "---\n")) {
                list.append(text);
            }
        }


        return list.toString();
    }

    public static String convertKeycodeToString(int keycode) {
        return switch (keycode) {
            case GLFW.GLFW_KEY_A -> "A";
            case GLFW.GLFW_KEY_B -> "B";
            case GLFW.GLFW_KEY_C -> "C";
            case GLFW.GLFW_KEY_D -> "D";
            case GLFW.GLFW_KEY_E -> "E";
            case GLFW.GLFW_KEY_F -> "F";
            case GLFW.GLFW_KEY_G -> "G";
            case GLFW.GLFW_KEY_H -> "H";
            case GLFW.GLFW_KEY_I -> "I";
            case GLFW.GLFW_KEY_J -> "J";
            case GLFW.GLFW_KEY_K -> "K";
            case GLFW.GLFW_KEY_L -> "L";
            case GLFW.GLFW_KEY_M -> "M";
            case GLFW.GLFW_KEY_N -> "N";
            case GLFW.GLFW_KEY_O -> "O";
            case GLFW.GLFW_KEY_P -> "P";
            case GLFW.GLFW_KEY_Q -> "Q";
            case GLFW.GLFW_KEY_R -> "R";
            case GLFW.GLFW_KEY_S -> "S";
            case GLFW.GLFW_KEY_T -> "T";
            case GLFW.GLFW_KEY_U -> "U";
            case GLFW.GLFW_KEY_V -> "V";
            case GLFW.GLFW_KEY_W -> "W";
            case GLFW.GLFW_KEY_X -> "X";
            case GLFW.GLFW_KEY_Y -> "Y";
            case GLFW.GLFW_KEY_Z -> "Z";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "CTRL";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "ALT";
            case GLFW.GLFW_KEY_KP_0 -> "P0";
            case GLFW.GLFW_KEY_KP_1 -> "P1";
            case GLFW.GLFW_KEY_KP_2 -> "P2";
            case GLFW.GLFW_KEY_KP_3 -> "P3";
            case GLFW.GLFW_KEY_KP_4 -> "P4";
            case GLFW.GLFW_KEY_KP_5 -> "P5";
            case GLFW.GLFW_KEY_KP_6 -> "P6";
            case GLFW.GLFW_KEY_KP_7 -> "P7";
            case GLFW.GLFW_KEY_KP_8 -> "P8";
            case GLFW.GLFW_KEY_KP_9 -> "P9";
            default -> "NONE";
        };
    }

    public static int convertKeycodeToString(String keycode) {
        return switch (keycode) {
            case "A" -> GLFW.GLFW_KEY_A;
            case "B" -> GLFW.GLFW_KEY_B;
            case "C" -> GLFW.GLFW_KEY_C;
            case "D" -> GLFW.GLFW_KEY_D;
            case "E" -> GLFW.GLFW_KEY_E;
            case "F" -> GLFW.GLFW_KEY_F;
            case "G" -> GLFW.GLFW_KEY_G;
            case "H" -> GLFW.GLFW_KEY_H;
            case "I" -> GLFW.GLFW_KEY_I;
            case "J" -> GLFW.GLFW_KEY_J;
            case "K" -> GLFW.GLFW_KEY_K;
            case "L" -> GLFW.GLFW_KEY_L;
            case "M" -> GLFW.GLFW_KEY_M;
            case "N" -> GLFW.GLFW_KEY_N;
            case "O" -> GLFW.GLFW_KEY_O;
            case "P" -> GLFW.GLFW_KEY_P;
            case "Q" -> GLFW.GLFW_KEY_Q;
            case "R" -> GLFW.GLFW_KEY_R;
            case "S" -> GLFW.GLFW_KEY_S;
            case "T" -> GLFW.GLFW_KEY_T;
            case "U" -> GLFW.GLFW_KEY_U;
            case "V" -> GLFW.GLFW_KEY_V;
            case "W" -> GLFW.GLFW_KEY_W;
            case "X" -> GLFW.GLFW_KEY_X;
            case "Y" -> GLFW.GLFW_KEY_Y;
            case "Z" -> GLFW.GLFW_KEY_Z;
            case "CTRL" -> GLFW.GLFW_KEY_RIGHT_CONTROL;
            case "ALT" -> GLFW.GLFW_KEY_RIGHT_ALT;
            case "P0" -> GLFW.GLFW_KEY_KP_0;
            case "P1" -> GLFW.GLFW_KEY_KP_1;
            case "P2" -> GLFW.GLFW_KEY_KP_2;
            case "P3" -> GLFW.GLFW_KEY_KP_3;
            case "P4" -> GLFW.GLFW_KEY_KP_4;
            case "P5" -> GLFW.GLFW_KEY_KP_5;
            case "P6" -> GLFW.GLFW_KEY_KP_6;
            case "P7" -> GLFW.GLFW_KEY_KP_7;
            case "P8" -> GLFW.GLFW_KEY_KP_8;
            case "P9" -> GLFW.GLFW_KEY_KP_9;
            default -> 0;
        };
    }

    // Post
    public static String Disposal(final SeverMode severMode) {
        if (severMode.isPath("/list")) {
            StringBuilder list = new StringBuilder();
            if (severMode.bool("Mode", "UiGui")) {
                String name = severMode.is("Name");
                for (final HaCd h : HackManager.getAll()) {
                    if (h.getName().equals(name)) {
                        for (final Value<?> x : h.getValues()) {
                            if (x.noShow()) {
                                continue;
                            }
                            if (x instanceof BooleanValue ff) {
                                list.append("BooleanValue").append(x.getName()).append("[#::#]").append(x.getValue()).append("[#Info#]")
                                        .append(ff.getInfo()).append("[#@@#]");
                            } else if (x instanceof NumberValue ff) {
                                list.append("NumberValue").append(x.getName()).append("[#::#]").append(x.getValue())
                                        .append("[").append("\"").append(ff.getMin()).append("\"").append("-")
                                        .append("\"").append(ff.getMax()).append("\"").append("]").append("[#@@#]");
                            } else if (x instanceof IntValue ff) {
                                list.append("IntValue").append(x.getName()).append("[#::#]").append(x.getValue())
                                        .append("[").append("\"").append(ff.getMin()).append("\"").append("-")
                                        .append("\"").append(ff.getMax()).append("\"").append("]").append("[#@@#]");
                            } else if (x instanceof ModeValue modeValue) {
                                list.append("ModeValue").append(x.getName()).append("[#::#]")
                                        .append(modeValue.getMode()).append("[#@@#]");
                            } else if (x instanceof TextValue) {
                                list.append("TextValue").append(x.getValue()).append("[#@@#]");
                            }
                        }
                        list.append("BindValue").append(h.getKeyString()).append("[#::#]").append("[#@@#]");
                        list.append("HackCategory").append(h.isCategory()).append("[#::#]");
                        break;
                    }
                }

            } else if (severMode.bool("Mode", "ModeValue")) {
                HaCd hack = HackManager.getSearch(severMode.is("Name"));
                if (hack != null) {
                    String modename = severMode.is("Value");
                    for (final Value<?> x : hack.getValues()) {
                        if (x instanceof ModeValue modeValue) {
                            if (x.getName().equals(modename)) {
                                for (final Mode modeS : modeValue.getModes()) {
                                    if (!modeS.isToggled())
                                        list.append(modeS.getName()).append("[#@@#]");
                                }
                                break;
                            }
                        }
                    }
                }

            } else if (severMode.bool("Mode", "NumberValue")) {
                HaCd hack = HackManager.getSearch(severMode.is("Name"));
                if (hack != null) {
                    String modename = severMode.is("Value");
                    for (final Value<?> x : hack.getValues()) {
                        if (x instanceof NumberValue || x instanceof IntValue) {
                            if (x.getName().equals(modename)) {
                                list.append(x.getValue()).append("[#@@#]");
                                break;
                            }
                        }
                    }
                }

            } else {
                String param1 = severMode.is("Mode");
                for (final Hack h : HackManager.getHack()) {
                    if (h.isCategory().equals(param1)) {
                        list.append("[#(").append(h.getName()).append("[#&&#]").append(h.isToggled())
                                .append(")#][#@@#]");
                    }
                }
            }
            return list.toString();
        } else if (severMode.isPath("/set") || severMode.isPath("/config")) {
            HackManager.severMode.add(severMode);
            return "Ok";
        }
        return "";
    }

    public void initialize() {
        new HackManager();
        new EventsHandler();

        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(23142);
                System.out.println("Server started. Listening on port 23142...");
                while (true) {
                    Socket socket = serverSocket.accept();

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    String inputLine;
                    StringBuilder request = new StringBuilder();
                    SeverMode severMode = new SeverMode();

                    while ((inputLine = in.readLine()) != null) {
                        if (inputLine.isEmpty()) {
                            break;
                        }
                        request.append(inputLine).append("\r\n");
                        int separatorIndex = inputLine.indexOf(":");
                        if (separatorIndex > 0) {
                            String name = inputLine.substring(0, separatorIndex).trim();
                            String value = inputLine.substring(separatorIndex + 1).trim();
                            severMode.add(name, value);
                        }
                    }

                    try {
                        StringTokenizer tokenizer = new StringTokenizer(request.toString());
                        String method = tokenizer.nextToken().toUpperCase();
                        String httpPath = tokenizer.nextToken();
                        severMode.httpPath = httpPath;
                        System.out.println("HelloSpace: " + httpPath);

                        if (method.equals("GET")) {
                            out.print("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n" + DisposalGet(severMode));
                        } else if (method.equals("POST")) {
                            out.print("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n" + Disposal(severMode));
                        }
                    } catch (NoSuchElementException ex) {
                        ex.fillInStackTrace();
                    }

                    out.flush();
                    socket.close();
                }
            } catch (IOException ex) {
                ex.fillInStackTrace();
            }
        }).start();
    }
}