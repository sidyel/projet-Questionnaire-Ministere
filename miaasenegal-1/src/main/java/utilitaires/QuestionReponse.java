package utilitaires;

import java.io.Serializable;

import org.entitee.Question;
import org.entitee.Reponse;

public class QuestionReponse implements Serializable{
	private static final long serialVersionUID = 1L;
	private Reponse rep;
	private Question quest;
	private Boolean item, noItem;
	
	public QuestionReponse() {
	
	}
	public QuestionReponse(Reponse rep, Question quest) {
		super();
		this.rep = rep;
		this.quest = quest;
	}
	public Reponse getRep() {
		return rep;
	}
	public void setRep(Reponse rep) {
		this.rep = rep;
	}
	public Question getQuest() {
		return quest;
	}
	public void setQuest(Question quest) {
		this.quest = quest;
	}
	public Boolean getItem() {
		return item;
	}
	public void setItem(Boolean item) {
		this.item = item;
	}
	public Boolean getNoItem() {
		return noItem;
	}
	public void setNoItem(Boolean noItem) {
		this.noItem = noItem;
	}

}
