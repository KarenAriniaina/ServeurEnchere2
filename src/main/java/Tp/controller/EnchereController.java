package Tp.controller;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import Tp.JSonData.JsonData;
import Tp.dao.Connexion;
import Tp.dao.ObjetBDD;
import Tp.model.Client;
import Tp.model.Enchere;
import Tp.model.Encherir;
import Tp.model.Photo;
import Tp.model.Configuration;

@RestController
public class EnchereController {

    @CrossOrigin
    @PostMapping("/Enchere")
    public JsonData AjoutEnchere(@RequestHeader("token") String token, @RequestHeader("idClient") String idClient,
            @RequestParam String Nom, @RequestParam String idCategorie, @RequestParam double Jour,
            @RequestParam double heure, @RequestParam double Minute,
            @RequestParam Double PrixDepart, @RequestParam String Description,@RequestBody(required = true) String image) throws Exception {
        JsonData json = new JsonData();
        Connection con = null;
        double Duree = Jour + (heure / 60) + (Minute / 1440);
        Client c = new Client();
        c.setToken(token);
        c.setIdClient(idClient);
        Gson g=new Gson();
        Photo[] lp=g.fromJson(image,Photo[].class);
        List<Photo> liste=new ArrayList<>();
        for(Photo p:lp){
            liste.add(p);
        }
        if (c.VerifToken()) {
            try {
                con = Connexion.getConnection();
                con.setAutoCommit(false);
                Configuration conf = (Configuration) new Configuration().Find(con)[0];
                Enchere e = new Enchere();
                e.setDescription(Description);
                e.setDuree(Duree);
                e.setIdCategorie(idCategorie);
                e.setIdClient(idClient);
                e.setNom(Nom);
                e.setPrixDepart(PrixDepart);
                e.setCommission(conf.getCommission());
                System.out.println(conf.getDureMin());
                if (conf.getDureMin() > e.getDuree() || conf.getDureeMax() < e.getDuree()) {
                    json.setData(null);
                    json.setMessage("Duree invalide");
                } else {
                    e.setPhotos(liste);
                    e.Create(con);
                    e.setIdEnchere("Enchere_" + Integer.toString(e.currentSequence(con)));
                    Object[] lc = new Object[1];
                    lc[0] = e;
                    json.setData(lc);
                    json.setMessage("Operation reussi");
                    con.commit();
                }

            } catch (Exception ex) {
                if (con != null)
                    con.rollback();
                json.setData(null);
                json.setMessage("Operation echoue");
                json.setStatus(false);
                json.setErreur(ex.getMessage());
            } finally {
                if (con != null)
                    con.close();

            }
        } else {
            json.setData(null);
            json.setMessage("Vous n'etes pas connecté");
        }
        return json;

    }

    @CrossOrigin
    @PostMapping("/Encherir/{idEnchere}")
    public JsonData ClientEncherir(@RequestHeader("token") String token, @RequestHeader("idClient") String idClient,
            @PathVariable("idEnchere") String idEnchere,
            @RequestParam("Montant") double Montant) throws Exception {
        JsonData json = new JsonData();
        Connection con = null;
        Client c = new Client();
        c.setToken(token);
        c.setIdClient(idClient);
        if (c.VerifToken()) {
            try {
                con = Connexion.getConnection();
                con.setAutoCommit(false);
                Enchere en = new Enchere();
                en.setIdEnchere(idEnchere);
                en.setNomTable("v_EnchereEnCours");
                ObjetBDD[] len = en.Find(con);
                if (len.length == 0) {
                    json.setData(null);
                    json.setMessage("Enchere deja cloturé");
                    return json;
                }
                en = (Enchere) len[0];
                if (en.getIdClient().equalsIgnoreCase(c.getIdClient())) {
                    json.setData(null);
                    json.setMessage("Vous ne pouvez faire l'enchere vu que vous etes proprietaire");
                    return json;
                }
                Encherir ec = new Encherir();
                ec.setIdEnchere(idEnchere);
                ec.getDernierEncherir(con);
                System.out.println(ec.getMontant());
                if (ec.getMontant() > 0) {
                    if (Montant <= ec.getMontant()) {
                        json.setData(null);
                        json.setMessage("Dernier mise superieur à la votre");
                        return json;
                    }
                }
                System.out.println("ato");
                if (Montant < en.getPrixDepart()) {
                    json.setData(null);
                    json.setMessage("Montant proposé inferieur au montant de depart");
                    return json;
                }
                c = (Client) c.Find(con)[0];
                if (c.getSolde() < Montant) {
                    json.setData(null);
                    json.setMessage("Votre solde est trop bas");
                    return json;
                }
                double solde = c.getSolde() - Montant;
                // rendre son solde au precedent nanao encherir
                if (ec.getIdClient() != null) {
                    Client last = new Client();
                    last.setIdClient(ec.getIdClient());
                    last = (Client) last.Find(con)[0];
                    double soldelast = last.getSolde() + ec.getMontant();
                    last.setSolde(soldelast);
                    last.Update(con);
                }
                // analana ny solde an re client miencherir
                c.setSolde(solde);
                c.Update(con);
                // Inserer-na amn zay re encherir
                ec.setIdClient(idClient);
                ec.setIdEncherir(null);
                ec.setMontant(Montant);
                ec.Create(con);
                json.setMessage("Operation reussi");

            } catch (Exception e) {
                if (con != null)
                    con.rollback();
                json.setData(null);
                json.setMessage("Operation echoue");
                json.setStatus(false);
                json.setErreur(e.getMessage());
            } finally {
                if (con != null)
                    con.close();

            }
        } else {
            json.setData(null);
            json.setMessage("Vous n'etes pas connecté");
        }
        return json;

    }

    /*
     * @CrossOrigin
     * 
     * @PostMapping("/DeleteEnchere/{idE}")
     * public JsonData DeleteCategorie(@RequestHeader("token") String
     * token, @RequestHeader("idAdmin") String idAdmin,
     * 
     * @PathParam("idE") String idEnchere) throws Exception {
     * JsonData json = new JsonData();
     * Connection con = null;
     * Admin a = new Admin();
     * a.setIdAdmin(idAdmin);
     * a.setToken(token);
     * if (!a.VerifToken()) {
     * try {
     * con = Connexion.getConnection();
     * con.setAutoCommit(false);
     * Enchere e = new Enchere();
     * e.setIdEnchere(idEnchere);
     * e.setNomTable("EnchereDelete");
     * e.setPrimaryKey("idEnchereDelete");
     * e.Create(con);
     * 
     * Object[] lc = new Object[1];
     * lc[0] = e;
     * json.setData(lc);
     * json.setMessage("Operation delete reussi");
     * con.commit();
     * } catch (Exception e) {
     * if (con != null)
     * con.rollback();
     * json.setData(null);
     * json.setMessage("Operation echoue");
     * json.setStatus(false);
     * json.setErreur(e.getMessage());
     * } finally {
     * if (con != null)
     * con.close();
     * }
     * } else {
     * json.setData(null);
     * json.setMessage("Vous n'etes pas connecté");
     * }
     * return json;
     * 
     * }
     */

    @CrossOrigin
    @RequestMapping("/Encheres")
    public JsonData ListeEnchere() throws Exception {
        JsonData json = new JsonData();
        try {
            //ObjetBDD[] lc = new Enchere().Find(null);
            Object[] lc=Enchere.getListeEnchere();
            json.setData(lc);
            json.setMessage("Operation select reussi");
        } catch (Exception e) {
            json.setData(null);
            json.setMessage("Operation echoue");
            json.setStatus(false);
            json.setErreur(e.getMessage() + "ef");
        }
        return json;
    }

    @CrossOrigin
    @GetMapping("/Enchere/{id}")
    public JsonData getEnchere(@PathVariable("id") String id) throws Exception {
        JsonData json = new JsonData();
        try {
            //ObjetBDD[] lc = new Enchere().Find(null);
            Enchere en=new Enchere();
            en.setIdEnchere(id);
            Object[] lc=Enchere.getEnchere(id);
            json.setData(lc);
            json.setMessage("Operation select reussi");
        } catch (Exception e) {
            json.setData(null);
            json.setMessage("Operation echoue");
            json.setStatus(false);
            json.setErreur(e.getMessage() + "ef");
        }
        return json;
    }

    @CrossOrigin
    @GetMapping("/HistoriqueEncheres/")
    public JsonData HitsoriqueEnchere(@RequestHeader("token") String token, @RequestHeader("idClient") String idClient)
            throws Exception {
        JsonData json = new JsonData();
        Client c = new Client();
        c.setToken(token);
        c.setIdClient(idClient);
        if (c.VerifToken()) {
            try {
                json.setData(c.getListeEnchere());
                json.setMessage("Operation select reussi");
            } catch (Exception e) {
                json.setData(null);
                json.setMessage("Operation echoue");
                json.setStatus(false);
                json.setErreur(e.getMessage());
            }
        } else {
            json.setData(null);
            json.setMessage("Vous n'etes pas connecté");
            json.setStatus(false);
        }
        return json;
    }

    @CrossOrigin
    @RequestMapping("/RechercheEncheres")
    public JsonData RechereAvance(@RequestParam String motsCle, @RequestParam String idCategorie,
            @RequestParam Double prixmin, @RequestParam Double prixmax,
            @RequestParam String Datedebut, @RequestParam String DateFin,@RequestParam int Statut) throws Exception {
        JsonData json = new JsonData();
        try {
            Object[] lc=Enchere.getEnchereByCritere(motsCle, idCategorie, prixmin, prixmax, Datedebut, DateFin, Statut);
            json.setData(lc);
            json.setMessage("Operation select reussi");
        } catch (Exception e) {
            json.setData(null);
            json.setMessage("Operation echoue");
            json.setStatus(false);
            json.setErreur(e.getMessage());
        }
        return json;
    }

    @CrossOrigin
    @PostMapping("/Test/")
    public String Test(@RequestBody(required = true) String image) {
        Gson g=new Gson();
        Photo[] lp=g.fromJson(image,Photo[].class);
        List<Photo> liste=new ArrayList<>();
        for(Photo p:lp){
            liste.add(p);
        }
        //List lp=g.fromJson(image,List.class);
        ////System.out.println(lp.get(0).toString());
        //System.out.println(lp.get(0).getBase64String());
        //Photo p=(Photo) lp.get(0);
        System.out.println(liste.get(0).getFormat());
        return g.toJson(liste);
    }

}
