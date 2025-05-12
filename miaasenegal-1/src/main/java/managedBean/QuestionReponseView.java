package managedBean;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

import org.dao.GenericDAO;
import org.entitee.Question;
import org.entitee.Questionnaire;
import org.entitee.Reponse;

import utilitaires.QuestionReponse;

import org.entitee.Employe;
import org.entitee.Itemquestion;

@Named("questionView")
@SessionScoped
public class QuestionReponseView implements Serializable {
	private Boolean actifOuiNonsaveReponse;
	private GenericDAO<Questionnaire> daoQuest = new GenericDAO<>(Questionnaire.class);

	private int questionnaireId;
	private List<Question> questions;
	private List<QuestionReponse> questionsreponses;

	// private List<Reponse> reponses=new ArrayList<Reponse>();
	private Map<Integer, String> answers = new HashMap<>();

	// DAO pour récupérer les questions et sauvegarder les réponses
	private GenericDAO<Question> daoQuestion = new GenericDAO<>(Question.class);
	private GenericDAO<Reponse> daoReponse = new GenericDAO<>(Reponse.class);
	private List<Itemquestion> itemquestions;
	private Employe currentEmploye = new Employe();

	@PostConstruct
	public void init() {
		// questionsreponses=new ArrayList<>();
		currentEmploye = (Employe) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("employe");
		System.out.println(currentEmploye.getPersonne().getPrenom());

		String idParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
		if (idParam != null)
			questionnaireId = Integer.parseInt(idParam);
		// Méthode personnalisée pour récupérer les questions du questionnaire
		/*
		 * questions = daoQuestion.findQuestionsByQuestionnaire(questionnaireId);
		 * reponses=daoReponse.trouverReponsesPourQuestionnaireEtUser(questionnaireId,
		 * currentEmploye.getId().getIdemploye()); QuestionReponse qr=null;
		 * if(questions!=null && questions.isEmpty()==false) { for(int
		 * i=0;i<questions.size();i++) { qr=new QuestionReponse(); Question
		 * q=questions.get(i); Reponse
		 * rep=daoReponse.findReponseQuestion(q.getIdquestion(),
		 * currentEmploye.getPersonne().getIdpersonne()); if(rep!=null) {
		 * qr.setQuest(q); qr.setRep(rep); questionsreponses.add(qr); } else
		 * if(rep==null) { qr.setQuest(q); rep=new Reponse(); qr.setRep(rep);
		 * questionsreponses.add(qr); } } } System.out.println(reponses.size()); }
		 * 
		 * for (Question q : questions) { itemquestions = new ArrayList<Itemquestion>();
		 * itemquestions=daoQuestion.finditemsQuestionsByQuestionnaire(q.getIdquestion()
		 * ); Set<Itemquestion> set = new HashSet<>(itemquestions);
		 * q.setItemquestions(set);
		 * 
		 * }
		 */ }

	public String colorValeurReponse(Reponse rep) {
		String color = "blue";
		if (rep != null) {
			if (rep.getValeur() != null) {
				if (rep.getValeur().equals("Oui"))
					color = "green";
				if (rep.getValeur().equals("Non"))
					color = "red";
			}
		}
		return color;
	}

	public void submitAnswers() {
		int k = 0;
		Employe currentUser = (Employe) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("employe");
		if (currentUser == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Utilisateur non authentifié."));
		}
		for (QuestionReponse q : questionsreponses) {
			String answerText = answers.get(q.getQuest().getIdquestion());
			if (/*answerText != null && !answerText.trim().isEmpty()*/ true) {
				Reponse rep = q.getRep();// Reponse();
				if (rep.getIdreponse() == 0) {
					k++;
					rep.setQuestion(q.getQuest());
					rep.setEmploye(currentUser);
					rep.setValeur(answerText);
					// Enregistrer la date de réponse au format souhaité
					rep.setDatereponse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
					rep.setEtat("Nouveau");
					// Sauvegarder la réponse en base via le DAO
					daoReponse.persist(rep);
					Question w = q.getQuest();
					if (w.getNombrereponses() == null)
						w.setNombrereponses(1);
					else if (w.getNombrereponses() != null)
						w.setNombrereponses(w.getNombrereponses() + 1);
					daoQuestion.merge(w);

				} else if (rep.getIdreponse() > 0) {
					k++;
					// rep.setQuestion(q.getQuest());
					// rep.setEmploye(currentUser);
					rep.setValeur(answerText);
					// Enregistrer la date de réponse au format souhaité
					rep.setDatereponse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
					rep.setEtat("MAJ");
					// Sauvegarder la réponse en base via le DAO
					daoReponse.merge(rep);
				}
			}
		}
		if (k > 0) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Sauvegarde", "Vos reponses sont bien enregistrées."));
		}
		/*
		 * FacesContext.getCurrentInstance().addMessage(null, new
		 * FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Réponse enregistrée"));
		 * return "acceuil?faces-redirect=true";
		 */
	}

	// Getters et Setters

	public int getQuestionnaireId() {
		return questionnaireId;
	}

	public void setQuestionnaireId(int questionnaireId) {
		this.questionnaireId = questionnaireId;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public Map<Integer, String> getAnswers() {
		return answers;
	}

	public void setAnswers(Map<Integer, String> answers) {
		this.answers = answers;
	}

	/*
	 * public List<Reponse> getReponses() { return reponses; }
	 * 
	 * public void setReponses(List<Reponse> reponses) { this.reponses = reponses; }
	 */

	public boolean utilisateurARepondu(int idQuestionnaire) {
		// Vérifier si l'utilisateur a répondu au questionnaire
		Questionnaire q=daoQuest.findById(idQuestionnaire);
        if(q!=null) {
        	if(q.getEtat()!=null) {
        		if(q.getEtat().equals("active"))
        			return false;
        		else return true;
        	}
        	else return true;
        }
		return true;//daoProjet.utilisateurARepondu(idQuestionnaire, currentEmploye.getId().getIdemploye());
	}

	public Boolean getActifOuiNonsaveReponse() {

		boolean w = this.utilisateurARepondu(questionnaireId);

		if (w == false)
			this.actifOuiNonsaveReponse = false;

		else
			this.actifOuiNonsaveReponse = true;

		return actifOuiNonsaveReponse;

	}

	public void setActifOuiNonsaveReponse(Boolean actifOuiNonsaveReponse) {
		this.actifOuiNonsaveReponse = actifOuiNonsaveReponse;
	}

	public List<QuestionReponse> getQuestionsreponses() {
		questionsreponses = new ArrayList<>();
		/*
		 * currentEmploye = (Employe)
		 * FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
		 * .get("employe"); String idParam =
		 * FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap
		 * ().get("id"); if (idParam != null) { questionnaireId =
		 * Integer.parseInt(idParam);
		 */
		// Méthode personnalisée pour récupérer les questions du questionnaire
		questions = daoQuestion.findQuestionsByQuestionnaire(questionnaireId);

		// reponses=daoReponse.trouverReponsesPourQuestionnaireEtUser(questionnaireId,
		// currentEmploye.getId().getIdemploye());
		QuestionReponse qr = null;
		if (questions != null && questions.isEmpty() == false) {
			for (int i = 0; i < questions.size(); i++) {
				qr = new QuestionReponse();
				Question q = questions.get(i);
				itemquestions = new ArrayList<Itemquestion>();
				itemquestions = daoQuestion.finditemsQuestionsByQuestionnaire(q.getIdquestion());
				if (itemquestions != null && itemquestions.isEmpty() == false) {
					Set<Itemquestion> set = new HashSet<>(itemquestions);
					q.setItemquestions(set);
					qr.setItem(true);
					qr.setNoItem(false);
				} else {
					qr.setItem(false);
					qr.setNoItem(true);
				}
				Reponse rep = daoReponse.findReponseQuestion(q.getIdquestion(),
						currentEmploye.getPersonne().getIdpersonne());
				System.out.println(currentEmploye.getPersonne().getPrenom());
				if (rep != null) {
					qr.setQuest(q);
					qr.setRep(rep);
					questionsreponses.add(qr);
				} else if (rep == null) {
					qr.setQuest(q);
					rep = new Reponse();
					qr.setRep(rep);
					questionsreponses.add(qr);
				}
			}
		}
		// }

		/*
		 * for (Question q : questions) { itemquestions = new ArrayList<Itemquestion>();
		 * itemquestions=daoQuestion.finditemsQuestionsByQuestionnaire(q.getIdquestion()
		 * ); Set<Itemquestion> set = new HashSet<>(itemquestions);
		 * q.setItemquestions(set);
		 * 
		 * }
		 */
		return questionsreponses;
	}

	public void setQuestionsreponses(List<QuestionReponse> questionsreponses) {
		this.questionsreponses = questionsreponses;
	}

}
