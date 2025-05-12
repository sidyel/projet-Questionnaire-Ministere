package managedBean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

import org.dao.GenericDAO;
import org.entitee.Question;
import org.entitee.Questionnaire;
import org.entitee.Reponse;
import org.entitee.Employe;
import org.entitee.Itemquestion;
import utilitaires.ItemPourcentage;

@Named("evaluationBean")
@ViewScoped
public class EvaluationBean implements Serializable {

	private int questionnaireId;
	private Questionnaire questionnaire;
	private List<Question> questions;
	private List<Employe> employesSansReponse;

	private Long nombre;
	// Map associant l'ID d'une question à la liste des pourcentages des réponses (y
	// compris "Aucune réponse")
	private Map<Integer, List<ItemPourcentage>> evaluationResults = new HashMap<>();

	// DAOs à adapter à votre architecture
	private GenericDAO<Questionnaire> daoQuestionnaire = new GenericDAO<>(Questionnaire.class);
	private GenericDAO<Question> daoQuestion = new GenericDAO<>(Question.class);
	private GenericDAO<Reponse> daoReponse = new GenericDAO<>(Reponse.class);
	private GenericDAO<Itemquestion> daoItem = new GenericDAO<>(Itemquestion.class);

	@PostConstruct
	public void init() {
		Object obj = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("questionnaireId");
		if (obj != null) {
			questionnaireId = (Integer) obj;
			questionnaire = daoQuestionnaire.findById(questionnaireId);
			questions = daoQuestion.findQuestionsByQuestionnaire(questionnaireId);
			employesSansReponse = daoQuestion.getEmployesSansReponsePourQuestionnaire(questionnaireId,
					daoQuestion.getInstitutionByQuestionnaireId(questionnaireId).getIdins());

			Long totalEligible = getTotalEligibleForQuestionnaire();
			if (totalEligible == null || totalEligible == 0) {
				nombre = 0L;
				return; // Évite la division par zéro
			}
			nombre = totalEligible;
			for (Question q : questions) {
				evaluationResults.put(q.getIdquestion(), daoQuestion.getItemPourcentages(q, totalEligible));
			}
		}
	}

	// Méthode pour obtenir le nombre total d'utilisateurs éligibles
	private Long getTotalEligibleForQuestionnaire() {
		Long count = daoQuestion.nombreDePersonneAyantReponduAuQuestonnaire(questionnaireId);
		return count; // Valeur d'exemple
	}

	// Getters
	public Questionnaire getQuestionnaire() {
		return questionnaire;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public Map<Integer, List<ItemPourcentage>> getEvaluationResults() {
		return evaluationResults;
	}

	public Long getNombre() {
		return nombre;
	}

	public void setNombre(Long nombre) {
		this.nombre = nombre;
	}

	public boolean isAllQuestionsEmpty() {
		for (Question q : questions) {
			if (evaluationResults.containsKey(q.getIdquestion())
					&& !evaluationResults.get(q.getIdquestion()).isEmpty()) {
				return false; // Il y a au moins une question avec des réponses
			}
		}
		return true; // Aucune question n'a de réponse
	}

	public List<Employe> getEmployesSansReponse() {
		return employesSansReponse;
	}

	public void setEmployesSansReponse(List<Employe> employesSansReponse) {
		this.employesSansReponse = employesSansReponse;
	}

}
