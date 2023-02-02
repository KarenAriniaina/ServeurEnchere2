package Tp.model;

import java.sql.Connection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.sql.Timestamp;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import Tp.dao.Connexion;
import Tp.dao.ObjetBDD;

public class Enchere extends ObjetBDD {
    private String idEnchere;
    private String Nom;
    private String idCategorie;
    private double Duree;
    private Double PrixDepart;
    private String Description;
    private String idClient;
    private Timestamp Date;
    private Timestamp DateFin;
    private Double Commission;
    private String Categorie;
    
    private List<Photo> photos;

    private List<Encherir> encherir;

    public List<Encherir> getEncherir() {
        return encherir;
    }

    public void setEncherir(List<Encherir> encherir) {
        this.encherir = encherir;
    }

    public String getIdEnchere() {
        return idEnchere;
    }

    public void setIdEnchere(String idEnchere) {
        this.idEnchere = idEnchere;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) {
        Nom = nom;
    }

    public String getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(String idCategorie) {
        this.idCategorie = idCategorie;
    }

    public double getDuree() {
        return Duree;
    }

    public Enchere() {
        this.setNomTable("Enchere");
        this.setPrimaryKey("idEnchere");
    }

    public void setDuree(double duree) {
        Duree = duree;
    }

    public Double getPrixDepart() {
        return PrixDepart;
    }

    public void setPrixDepart(Double prixDepart) {
        PrixDepart = prixDepart;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public Timestamp getDate() {
        return Date;
    }

    public void setDate(Timestamp date) {
        Date = date;
    }

    public Double getCommission() {
        return Commission;
    }

    public void setCommission(Double commission) {
        Commission = commission;
    }

    @Override
    public void Create(Connection c) throws Exception {
        try {
            super.Create(c);
            String currentId = "Enchere_" + Integer.toString(this.currentSequence(c));
            this.setIdEnchere(currentId);
            Enchere en = (Enchere) this.Find(c)[0];
            MongoDatabase database = Connexion.getMongoConnection();
            MongoCollection<Document> collection = database.getCollection("Enchere");
            Document filtre = new Document("idEnchere", this.getIdEnchere());
            Instant instant = Instant.ofEpochMilli(en.getDate().getTime());
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            int DureeEnSeconde = (int) Duree * 3600 * 24;
            localDateTime = localDateTime.plusSeconds(DureeEnSeconde);
            filtre.append("datedebut", en.getDate().toString());
            filtre.append("datefin", Timestamp.valueOf(localDateTime).toString());
            filtre.append("prixdepart", this.getPrixDepart()).append("description", this.getDescription()).append("Nom",
                    this.getNom());
            Categorie ca = new Categorie();
            ca.setIdCategorie(this.getIdCategorie());
            ca = (Categorie) ca.Find(c)[0];
            filtre.append("Categorie", ca.getDesignation());
            filtre.append("idCategorie", this.getIdCategorie());
            filtre.append("idClient", this.getIdClient());
            this.setEncherir(new ArrayList<>());
            filtre.append("photos", this.getPhotos());
            filtre.append("encherir", this.getEncherir());
            collection.insertOne(filtre);
        } catch (Exception ex) {
            if (c != null)
                c.rollback();
            throw ex;
        }
    }

    public static Object[] getListeEnchere() throws Exception {
        MongoDatabase database = Connexion.getMongoConnection();
        MongoCollection<Document> collection = database.getCollection("Enchere");
        Enchere[] liste = null;
        ArrayList<Enchere> le = new ArrayList<>();
        for (Document doc : collection.find()) {
            Enchere e = new Enchere();
            e.setDate(Timestamp.valueOf(doc.getString("datedebut")));
            e.setDateFin(Timestamp.valueOf(doc.getString("datefin")));
            e.setIdEnchere(doc.getString("idEnchere"));
            e.setNom(doc.getString("Nom"));
            e.setIdCategorie(doc.getString("idCategorie"));
            e.setCategorie(doc.getString("Categorie"));
            e.setPrixDepart(doc.getDouble("prixdepart"));
            e.setDescription(doc.getString("description"));
            e.setEncherir((List<Encherir>) doc.get("encherir"));
            e.setPhotos((List<Photo>) doc.get("photos"));
            le.add(e);
        }
        return le.toArray();
        /*
         * MongoDatabase database = Connexion.getMongoConnection();
         * MongoCollection<Document> collection = database.getCollection("Enchere");
         * ArrayList<Document> docu=new ArrayList<>();
         * ArrayList<String> liste=new ArrayList<>();
         * //FindIterable<Document> ld = ;
         * for (Document doc : collection.find()) {
         * System.out.println(doc.toJson());
         * liste.add(doc.toJson());
         * }
         */
    }

    public static Object[] getEnchere(String id) throws Exception {
        MongoDatabase database = Connexion.getMongoConnection();
        MongoCollection<Document> collection = database.getCollection("Enchere");
        ArrayList<Enchere> le = new ArrayList<>();
        for (Document doc : collection.find().filter(new Document("idEnchere", id))) {
            Enchere e = new Enchere();
            e.setDate(Timestamp.valueOf(doc.getString("datedebut")));
            e.setDateFin(Timestamp.valueOf(doc.getString("datefin")));
            e.setIdEnchere(doc.getString("idEnchere"));
            e.setNom(doc.getString("Nom"));
            e.setIdCategorie(doc.getString("idCategorie"));
            e.setPrixDepart(doc.getDouble("prixdepart"));
            e.setCategorie(doc.getString("Categorie"));
            e.setDescription(doc.getString("description"));
            e.setEncherir((List<Encherir>) doc.get("encherir"));
            e.setPhotos((List<Photo>) doc.get("photos"));
            le.add(e);
        }
        return le.toArray();
    }

    public Timestamp getDateFin() {
        return DateFin;
    }

    public void setDateFin(Timestamp dateFin) {
        DateFin = dateFin;
    }

    public static Object[] getEnchereByCritere(String motsCle, String idCategorie,
            Double prixmin, Double prixmax,
            String Datedebut, String DateFin, int Statut) throws Exception {
        MongoDatabase database = Connexion.getMongoConnection();
        MongoCollection<Document> collection = database.getCollection("Enchere");
        ArrayList<Enchere> le = new ArrayList<>();
        // Get the current date
        Instant now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
        String nowString = now.atOffset(ZoneOffset.UTC).format(formatter);
        // Find documents where the date field is greater than the current date
        // collection.find(Filters.gte("datefin",nowString)) statut
        // collection.find(Filters.regex("Nom",Pattern.compile(".*encheef.*",
        // Pattern.CASE_INSENSITIVE))) motsCle
        // collection.find(Filters.eq("idCategorie", "Categorie_3")) categorie
        // collection.find(Filters.gte("prixdepart",20000)) prix min
        // collection.find(Filters.lte("prixdepart",20000)) prix max
        Document d=new Document();
        if(Statut==1) d.append("datefin", new  Document().append("$gte", nowString));   //en cours
        if(Statut==2) d.append("datefin", new  Document().append("$lte", nowString));   //tapitra
        if(!motsCle.equalsIgnoreCase("")) d.append("Nom",new Document().append("$regex", ".*"+motsCle+".*") .append("$options","i"));
        if(!idCategorie.equalsIgnoreCase("")) d.append("idCategorie",idCategorie);
        Document prix=new Document();
        if(prixmax!=0) prix.append("$lte", prixmax);
        if(prixmin!=0) prix.append("$gte", prixmin);
        if(prixmax!=0 || prixmin !=0) d.append("prixdepart",prix);
        Document date=new Document();
        if(!Datedebut.equalsIgnoreCase("")) date.append("$gte", Datedebut);
        if(!DateFin.equalsIgnoreCase("")) date.append("$lte", DateFin);
        if(!Datedebut.equalsIgnoreCase("") ||  !DateFin.equalsIgnoreCase("") ) d.append("datedebut",date);

        for (Document doc : collection.find().filter(d)) {
            Enchere e = new Enchere();
            e.setDate(Timestamp.valueOf(doc.getString("datedebut")));
            e.setDateFin(Timestamp.valueOf(doc.getString("datefin")));
            e.setIdEnchere(doc.getString("idEnchere"));
            e.setNom(doc.getString("Nom"));
            e.setCategorie(doc.getString("Categorie"));
            e.setIdCategorie(doc.getString("idCategorie"));
            e.setPrixDepart(doc.getDouble("prixdepart"));
            e.setDescription(doc.getString("description"));
            e.setEncherir((List<Encherir>) doc.get("encherir"));
            e.setPhotos((List<Photo>) doc.get("photos"));
            le.add(e);
        }
        return le.toArray();
    }

    public String getCategorie() {
        return Categorie;
    }

    public void setCategorie(String categorie) {
        Categorie = categorie;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

}
