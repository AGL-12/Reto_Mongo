package pool;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import java.util.ResourceBundle;

public class MongoConnection {

    private static MongoClient mongoClient = null;
    private static MongoDatabase database = null;

    // Bloque estático para inicializar UNA SOLA VEZ
    static {
        try {
            ResourceBundle configFile = ResourceBundle.getBundle("config.classConfig");

            String urlBase = configFile.getString("Conn");
            String dbName = configFile.getString("DBName");

            // Leemos los parámetros
            String minPool = configFile.getString("MinPoolSize");
            String maxPool = configFile.getString("MaxPoolSize");
            String maxWait = configFile.getString("MaxWaitTime"); // Para waitQueueTimeoutMS
            String maxIdle = configFile.getString("MaxIdleTime"); // Para maxIdleTimeMS

            // Construimos la URL con TODOS los parámetros
            // Nota: connectTimeoutMS es para conectar al socket TCP
            // waitQueueTimeoutMS es para esperar turno en el pool (lo que hacías antes)
            String finalConnectionString = String.format(
                    "%s/?minPoolSize=%s&maxPoolSize=%s&waitQueueTimeoutMS=%s&maxIdleTimeMS=%s&connectTimeoutMS=2000",
                    urlBase, minPool, maxPool, maxWait, maxIdle
            );

            System.out.println("Configurando Mongo con: " + finalConnectionString);

            mongoClient = MongoClients.create(finalConnectionString);
            database = mongoClient.getDatabase(dbName);

            System.out.println("--- Conexión Establecida ---");

        } catch (Exception e) {
            System.err.println("Error iniciando MongoDB: " + e.getMessage());
        }
    }

    public static MongoDatabase getDatabase() {
        return database;
    }

    // Método para cerrar al apagar la app (opcional)
    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
