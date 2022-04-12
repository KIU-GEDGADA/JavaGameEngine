package graphics;

import utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32C.GL_GEOMETRY_SHADER;

public class Shader {
    private final String filePath;
    private int shaderType;
    private String shaderSource;
    private int shaderID;

    public Shader(String filePath) {
        this.filePath = filePath;
        try {
            String source = FileUtils.readFile(filePath);
            String[] splitString = source.split("(//type)( )+([a-zA-Z]+)");

            int index = source.indexOf("//type") + 6;
            int endOfLine = source.indexOf("\r\n", index);
            String pattern = source.substring(index, endOfLine).trim();

            shaderSource = splitString[1];
            switch (pattern) {
                case "vertex":
                    shaderType = GL_VERTEX_SHADER;
                    break;
                case "fragment":
                    shaderType = GL_FRAGMENT_SHADER;
                    break;
                case "geometry":
                    shaderType = GL_GEOMETRY_SHADER;
                    break;
                default:
                    throw new IOException("Unexpected shader type " + pattern + " in " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open Shader " + filePath + " for reading";
        }
    }

    public void compile() {
        shaderID = glCreateShader(shaderType);
        glShaderSource(shaderID, shaderSource);
        glCompileShader(shaderID);

        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            int len = glGetShaderi(shaderID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: " + filePath + " shader could not be compiled");
            System.out.println(glGetShaderInfoLog(shaderID, len));
            assert false : "";
            System.exit(-1);
        }
        System.out.println("Compiled " + filePath + " shader");
    }

    public int getShaderID() {
        return shaderID;
    }

    public int getShaderType() {
        return shaderType;
    }
}