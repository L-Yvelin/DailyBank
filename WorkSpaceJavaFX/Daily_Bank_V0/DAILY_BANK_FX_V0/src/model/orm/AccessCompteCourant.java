package model.orm;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import application.control.ExceptionDialog;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.exception.ApplicationException;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

public class AccessCompteCourant {

	public AccessCompteCourant() {
	}

	/**
	 * Recherche des CompteCourant d'un client à partir de son id.
	 *
	 * @param idNumCli id du client dont on cherche les comptes
	 * @return Tous les CompteCourant de idNumCli (ou liste vide)
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public ArrayList<CompteCourant> getCompteCourants(int idNumCli)
			throws DataAccessException, DatabaseConnexionException {
		ArrayList<CompteCourant> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM CompteCourant where idNumCli = ?";
			query += " ORDER BY idNumCompte";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumCli);
			System.err.println(query);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idNumCompte = rs.getInt("idNumCompte");
				int debitAutorise = rs.getInt("debitAutorise");
				double solde = rs.getDouble("solde");
				String estCloture = rs.getString("estCloture");
				int idNumCliTROUVE = rs.getInt("idNumCli");

				alResult.add(new CompteCourant(idNumCompte, debitAutorise, solde, estCloture, idNumCliTROUVE));
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.SELECT, "Erreur accès", e);
		}

		return alResult;
	}

	
	  public static ArrayList<Operation> getViewListOperationsDunCompte(int idNumCompte) throws DataAccessException, DatabaseConnexionException {
			ArrayList<Operation> alResult = new ArrayList<>();

			try {
				Connection con = LogToDatabase.getConnexion();
				String query = "SELECT * FROM Operation where idNumCompte = ? AND to_char(dateOp, 'MM') = to_char(sysdate, 'MM') ";
				query += " ORDER BY dateOp";

				PreparedStatement pst = con.prepareStatement(query);
				pst.setInt(1, idNumCompte);

				ResultSet rs = pst.executeQuery();
				while (rs.next()) {
					int idOperation = rs.getInt("idOperation");
					double montant = rs.getDouble("montant");
					Date dateOp = rs.getDate("dateOp");
					Date dateValeur = rs.getDate("dateValeur");
					int idNumCompteTrouve = rs.getInt("idNumCompte");
					String idTypeOp = rs.getString("idTypeOp");

					alResult.add(new Operation(idOperation, montant, dateOp, dateValeur, idNumCompteTrouve, idTypeOp));
				}
				rs.close();
				pst.close();
				return alResult;
			} catch (SQLException e) {
				throw new DataAccessException(Table.Operation, Order.SELECT, "Erreur accès", e);
			}
		}
	
	/**
	 * Recherche de l'ensemble des comptes courants ouverts présents pour un client
	 *
	 * @return Les comptes ou null si non trouvé
	 * @return Tous les CompteCourant d'un client (ou liste vide)
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public ArrayList<CompteCourant> getComptesOuverts(int idNumCli)
            throws DataAccessException, DatabaseConnexionException {
        ArrayList<CompteCourant> alResult = new ArrayList<>();
 
        try {
            Connection con = LogToDatabase.getConnexion();
            
            String query;
            
                query = "SELECT * FROM CompteCourant where idNumCli = ? AND estCloture = 'N' ORDER BY idNumCompte";
                
                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, idNumCli);
                
                System.err.println(query);
 
 
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int idNumCompte = rs.getInt("idNumCompte");
                int debitAutorise = rs.getInt("debitAutorise");
                double solde = rs.getDouble("solde");
                String estCloture = rs.getString("estCloture");
                int idNumCliO = rs.getInt("idNumCli");
  
                alResult.add(new CompteCourant(idNumCompte,debitAutorise,solde,estCloture,idNumCliO));
            }
            rs.close();
            pst.close();
        } catch (SQLException e) {
            throw new DataAccessException(Table.Client, Order.SELECT, "Erreur accès", e);
        }
 
        return alResult;
    }
	/**
	 * Recherche de l'ensemble des comptes courants présents aux différents clients
	 *
	 * @return Les comptes ou null si non trouvé
	 * @return Tous les CompteCourant de tous les clients (ou liste vide)
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	
	public ArrayList<CompteCourant> getTousLesComptes()
            throws DataAccessException, DatabaseConnexionException {
        ArrayList<CompteCourant> alResult = new ArrayList<>();

        try {
            Connection con = LogToDatabase.getConnexion();
            PreparedStatement pst;
            
            String query;
            
                query = "SELECT * FROM COMPTECOURANT WHERE estCloture = 'N' ORDER BY idNumCompte ";
                
                pst = con.prepareStatement(query);


            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int idNumCompte = rs.getInt("idNumCompte");
                int debitAutorise = rs.getInt("debitAutorise");
                double solde = rs.getDouble("solde");
                String estCloture = rs.getString("estCloture");
                int idNumCli = rs.getInt("idNumCli");


                alResult.add(
                        new CompteCourant(idNumCompte,debitAutorise,solde,estCloture,idNumCli));
            }
            rs.close();
            pst.close();
        } catch (SQLException e) {
            throw new DataAccessException(Table.Client, Order.SELECT, "Erreur accès", e);
        }

        return alResult;
    }
	
	/**
	 * Recherche d'un CompteCourant à partir de son id (idNumCompte).
	 *
	 * @param idNumCompte id du compte (clé primaire)
	 * @return Le compte ou null si non trouvé
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public CompteCourant getCompteCourant(int idNumCompte)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			CompteCourant cc;

			Connection con = LogToDatabase.getConnexion();

			String query = "SELECT * FROM CompteCourant where" + " idNumCompte = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumCompte);

			System.err.println(query);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				int idNumCompteTROUVE = rs.getInt("idNumCompte");
				int debitAutorise = rs.getInt("debitAutorise");
				double solde = rs.getDouble("solde");
				String estCloture = rs.getString("estCloture");
				int idNumCliTROUVE = rs.getInt("idNumCli");

				cc = new CompteCourant(idNumCompteTROUVE, debitAutorise, solde, estCloture, idNumCliTROUVE);
			} else {
				rs.close();
				pst.close();
				return null;
			}

			if (rs.next()) {
				throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant, Order.SELECT,
						"Recherche anormale (en trouve au moins 2)", null, 2);
			}
			rs.close();
			pst.close();
			return cc;
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.SELECT, "Erreur accès", e);
		}
	}
	
	/**
	 * Permet de cloturer un compte bancaire (met son solde à zéro puis met l'état du compte à estClotue = "O"
	 * 
	 * @param compteAsupprimer IN : le compte en question à cloturer
	 * @throws SQLException
	 * @throws ApplicationException
	 */
	public  void cloturerCompte(CompteCourant compteAsupprimer) throws SQLException, ApplicationException {		

		Connection con = LogToDatabase.getConnexion(); //Connexion à la base de données
		
		String query = "UPDATE COMPTECOURANT SET ESTCLOTURE = 'O' , SOLDE = 0.00 WHERE IDNUMCOMPTE = ?";
		
		PreparedStatement pst = con.prepareStatement(query);
		pst.setInt(1, compteAsupprimer.idNumCompte);
		
		int result = pst.executeUpdate();
		pst.close();

		if (result != 1) {
			con.rollback();
			throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant, Order.UPDATE,
					"Insert anormal (insert de moins ou plus d'une ligne)", null, result);
		}else {
			con.commit();
		}

		if (Math.random() < -1) {
			throw new ApplicationException(Table.CompteCourant, Order.UPDATE, "todo : test exceptions", null);
		}

	}

	/**
	 * Mise à jour d'un CompteCourant.
	 *
	 * cc.idNumCompte (clé primaire) doit exister seul cc.debitAutorise est mis à
	 * jour cc.solde non mis à jour (ne peut se faire que par une opération)
	 * cc.idNumCli non mis à jour (un cc ne change pas de client)
	 *
	 * @param cc IN cc.idNumCompte (clé primaire) doit exister seul
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 * @throws ManagementRuleViolation
	 */
	public void updateCompteCourant(CompteCourant cc) throws RowNotFoundOrTooManyRowsException, DataAccessException,
			DatabaseConnexionException, ManagementRuleViolation {
		try {

			CompteCourant cAvant = this.getCompteCourant(cc.idNumCompte);
			if (cc.debitAutorise > 0) {
				cc.debitAutorise = -cc.debitAutorise;
			}
			if (cAvant.solde < cc.debitAutorise) {
				throw new ManagementRuleViolation(Table.CompteCourant, Order.UPDATE,
						"Erreur de règle de gestion : solde à découvert", null);
			}
			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE CompteCourant SET " + "debitAutorise = ? " + "WHERE idNumCompte = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, cc.debitAutorise);
			pst.setInt(2, cc.idNumCompte);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();
			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant, Order.UPDATE,
						"Update anormal (update de moins ou plus d'une ligne)", null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.UPDATE, "Erreur accès", e);
		}
	}
	
}
