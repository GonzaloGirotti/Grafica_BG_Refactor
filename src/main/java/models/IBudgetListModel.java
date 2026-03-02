package models;

import utils.Budget;
import utils.databases.hibernate.entities.Presupuestos;

import java.util.ArrayList;

public interface IBudgetListModel {
    ArrayList<Presupuestos> getBudgetsFromDB();
}
