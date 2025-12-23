package com.drawmasterpencil.hedgso4nik.model;


public class LessonItem {

	private int id;

	private String taskNameEn;
	int nSteps; // number of total steps


	public LessonItem() {
		id = 0;
		taskNameEn = "";
		nSteps		= 0;
	}

	public LessonItem(int id, String taskNameEn, int nSteps) {
		this.id = id;
		this.taskNameEn = taskNameEn;
		this.nSteps = nSteps;
	}


	public String getTaskName() {
		return taskNameEn;
	}

	public int getSteps() {
		return nSteps;
	}
}
