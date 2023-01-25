package Tp.controller;

import java.sql.Connection;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Tp.JSonData.JsonData;
import Tp.dao.Connexion;
import Tp.dao.ObjetBDD;
import Tp.model.Admin;
import Tp.model.Configuration;

@RestController
public class ConfigurationController {
    /*
     * @CrossOrigin
     * 
     * @GetMapping("Configurations/")
     * public JsonData UpdateCategorie() throws Exception {
     * JsonData json = new JsonData();
     * try {
     * Configuration c=new Configuration();
     * c.getConfig();
     * } catch (Exception e) {
     * json.setData(null);
     * json.setMessage("Operation echoue");
     * json.setStatus(false);
     * json.setErreur(e.getMessage());
     * }
     * return json;
     * 
     * }
     */
    @CrossOrigin
    @PostMapping("Configuration/")
    public JsonData UpdateConfig(@RequestHeader("token") String token, @RequestHeader("idAdmin") String idAdmin,
            @RequestParam("dureemin") double dureemin, @RequestParam("dureemax") double dureemax,
            @RequestParam("commission") double commission) throws Exception {
        JsonData json = new JsonData();
        Connection con = null;
        Admin a = new Admin();
        a.setIdAdmin(idAdmin);
        a.setToken(token);
        if (a.VerifToken()) {
            try {
                con = Connexion.getConnection();
                Configuration c = new Configuration();
                c.setIdConfiguration("Configuration_1");
                ObjetBDD[] lc = c.Find(con);
                c.setDureeMax(dureemax);
                c.setDureMin(dureemin);
                c.setCommission(commission);
                con.setAutoCommit(false);
                if (lc.length == 0)
                    c.Create(con);
                else
                    c.Update(con);
                con.commit();
                json.setStatus(true);
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

    @CrossOrigin
    @GetMapping("Configurations/")
    public JsonData ListeConfig(@RequestHeader("token") String token, @RequestHeader("idAdmin") String idAdmin) throws Exception {
        JsonData json = new JsonData();
        Connection con = null;
        Admin a = new Admin();
        a.setIdAdmin(idAdmin);
        a.setToken(token);
        if (a.VerifToken()) {
            try {
                con = Connexion.getConnection();
                Configuration c = new Configuration();
                ObjetBDD[] lc = c.Find(con);
                json.setData(lc);
                json.setMessage("Operation Reussi");
            } catch (Exception e) {
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


}
