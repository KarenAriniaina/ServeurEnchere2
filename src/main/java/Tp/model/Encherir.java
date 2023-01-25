package Tp.model;

import java.sql.Connection;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import Tp.dao.Connexion;
import Tp.dao.ObjetBDD;

public class Encherir extends ObjetBDD {
    private String idEncherir;
    private String idEnchere;
    private String idClient;
    private double Montant;

    public Encherir() {
        this.setNomTable("Encherir");
        this.setPrimaryKey("idEncherir");
    }

    public String getIdEncherir() {
        return idEncherir;
    }

    public void setIdEncherir(String idEncherir) {
        this.idEncherir = idEncherir;
    }

    public String getIdEnchere() {
        return idEnchere;
    }

    public void setIdEnchere(String idEnchere) {
        this.idEnchere = idEnchere;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public double getMontant() {
        return Montant;
    }

    public void setMontant(double montant) {
        Montant = montant;
    }

    @Override
    public void Create(Connection c) throws Exception {
        // TODO Auto-generated method stub
        this.setNomTable("Encherir");
        super.Create(c);
        Client client=new Client();
        client.setIdClient(this.getIdClient());
        client=(Client) client.Find(c)[0];
        MongoDatabase database = Connexion.getMongoConnection();
        MongoCollection<Document> collection = database.getCollection("Enchere");
        Document encherir = new Document("Client",client.getNom() )
                    .append("Montant encheri", this.getMontant());
        Document update = new Document("$push", new Document("encherir", encherir));
        System.out.println(update.toJson());
        Document filtre=    new Document("idEnchere",this.getIdEnchere());
        System.out.println(filtre.toJson());
        collection.updateOne(filtre, update);
    }

    public void getDernierEncherir(Connection con) throws Exception{
        this.setNomTable("v_DernierEncherirEnchere");
        ObjetBDD[] le=this.Find(con);
        if(le.length!=0){
            Encherir en=(Encherir) le[0];
            this.setMontant(en.getMontant());
        }
    }
}
