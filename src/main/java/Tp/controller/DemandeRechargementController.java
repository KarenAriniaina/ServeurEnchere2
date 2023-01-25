package Tp.controller;

import java.sql.Connection;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Tp.JSonData.JsonData;
import Tp.dao.Connexion;
import Tp.dao.ObjetBDD;
import Tp.model.Admin;
import Tp.model.Client;
import Tp.model.DemandeRechargement;

@RestController
public class DemandeRechargementController {

    @CrossOrigin
    @GetMapping("/DemandeRechargements")
    public JsonData ListeDemandeRechargementNonValide(@RequestHeader("token") String token,
            @RequestHeader("idAdmin") String idAdmin) throws Exception {
        JsonData json = new JsonData();
        Admin a = new Admin();
        a.setIdAdmin(idAdmin);
        a.setToken(token);
        if (a.VerifToken()) {
            try {
                DemandeRechargement d = new DemandeRechargement();
                d.setNomTable("v_Rechargement");
                d.setStatut(1);
                ObjetBDD[] lc = d.Find(null);
                json.setData(lc);
                json.setMessage("Operation reussi");
            } catch (Exception e) {
                json.setData(null);
                json.setMessage("Operation echoue");
                json.setStatus(false);
                json.setErreur(e.getMessage());
            }
        } else {
            json.setData(null);
            json.setMessage("Vous n'etes pas connecté");
        }
        return json;

    }

    @CrossOrigin
    @GetMapping("/DemandeRechargements/{id}")
    public JsonData ValideRechargement(@RequestHeader("token") String token,
            @RequestHeader("idAdmin") String idAdmin, @PathVariable("id") String id) throws Exception {
        JsonData json = new JsonData();
        Connection con = null;
        Admin a = new Admin();
        a.setIdAdmin(idAdmin);
        a.setToken(token);
        if (a.VerifToken()) {
            try {
                con = Connexion.getConnection();
                con.setAutoCommit(false);
                DemandeRechargement d = new DemandeRechargement();
                d.setIdDemandeRechargement(id);
                System.out.println(d.getIdDemandeRechargement());
                d = (DemandeRechargement) d.Find(con)[0];
                Client c = new Client();
                c.setIdClient(d.getIdClient());
                c = (Client) c.Find(con)[0];
                double montant = c.getSolde();
                montant += d.getMontant();
                c.setSolde(montant);
                d.setStatut(2);
                d.Update(con);
                c.Update(con);
                Object[] ld = new Object[1];
                ld[0] = d;
                con.commit();
                json.setData(ld);
                json.setMessage("Operation reussi");
            } catch (Exception e) {
                if (con != null)
                    con.rollback();
                json.setMessage(e.getMessage());
                json.setStatus(false);
            } finally {
                if (con != null)
                    con.close();
            }
        }else{
            json.setData(null);
            json.setMessage("Vous n'etes pas connecté");   
        }
        return json;

    }

    @CrossOrigin
    @PostMapping("/DemandeRechargement")
    public JsonData InsererRechargement(@RequestHeader("token") String token,
            @RequestHeader("idClient") String idClient, @RequestParam("montant") double montant) throws Exception {
        JsonData json = new JsonData();
        Connection con = null;
        Client c = new Client();
        c.setToken(token);
        System.out.println(c.getToken());
        c.setIdClient(idClient);
        System.out.println(c.VerifToken());
        if (c.VerifToken()) {
            try {
                con = Connexion.getConnection();
                con.setAutoCommit(false);
                DemandeRechargement d = new DemandeRechargement();
                d.setMontant(montant);
                d.setIdClient(idClient);
                d.setStatut(1);
                d.Create(con);
                Object[] ld = new Object[1];
                ld[0] = d;
                json.setData(ld);
                json.setMessage("Operation reussi");
                con.commit();
            } catch (Exception e) {
                if (con != null)
                    con.rollback();
                json.setData(null);
                json.setMessage("Operation echoue");
                json.setStatus(false);
                json.setErreur(e.getMessage());
            }
        } else {
            json.setData(null);
            json.setMessage("Client non connecté");
        }
        return json;

    }
}