/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.pdsw.samples.services.impl;

import com.google.inject.Inject;
import edu.eci.pdsw.samples.dao.ConsultaSolicitudCancelacionDAO;
import edu.eci.pdsw.samples.dao.DecanoDAO;
import edu.eci.pdsw.samples.dao.EstudianteDAO;
import edu.eci.pdsw.samples.dao.PersistenceException;
import edu.eci.pdsw.samples.entities.*;

import edu.eci.pdsw.samples.entities.Asignatura;
import edu.eci.pdsw.samples.entities.Estudiante;
import edu.eci.pdsw.samples.entities.PlanEstudios;
import edu.eci.pdsw.samples.entities.ProgramaAcademico;
import edu.eci.pdsw.samples.dao.mybatis.ConsultaSolicitudCancelacionDAOMyBatis;

import edu.eci.pdsw.samples.services.CalculadorDeImpacto;



import edu.eci.pdsw.samples.services.ServiciosCancelaciones;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mybatis.guice.transactional.Transactional;

import edu.eci.pdsw.samples.services.ExcepcionSolicitudes;
import edu.eci.pdsw.samples.services.ExtractorPlanEstudios;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author 2104481
 */
public  class ServiciosCancelacionesImpl implements ServiciosCancelaciones{
    
    @Inject
    private DecanoDAO d;
    
    @Inject
    private EstudianteDAO de;
    
    @Inject
    private ConsultaSolicitudCancelacionDAO con;
     
    
    private final Map<Tupla<Integer, String>, Estudiante> estudiantes;
    private final List<Asignatura> asignaturasPlanEstudios;
    private final List<Asignatura> vistasActualmente;
    
    @Inject
    private CalculadorDeImpacto calculadorDeImpacto;   
    
    @Inject
    private ExtractorPlanEstudios extractorPlanEstudios;
    
    
    public ServiciosCancelacionesImpl(){
        this.estudiantes = new LinkedHashMap<>();
        asignaturasPlanEstudios = new LinkedList<>();
        vistasActualmente= new LinkedList<>();
        cargarDatosPrueba();
        cargarDatosEstaticosGrafo();

    }        
    
    /**
     * Algoritmo de cálculo de impacto que se limita
       a indicar, dado el estudiante y la asignatura a cancelar, 
       cuantos créditos académicos tendría pendiente por ver.
     * @param e The student.
     * @param materia The subject that you want cancel.
     * @throws edu.eci.pdsw.samples.services.ExcepcionSolicitudes si el estudiante o la asignatura no existen
     * @return the int
     */
    public int calcularImpacto(Asignatura asig, PlanEstudios plan) throws ExcepcionSolicitudes{
        return calculadorDeImpacto.calcularImpacto(asig, plan);
    }    
    /**
     * Extrae el plan de estudios del estudiante
     * @param e el estudiante que tiene el plan de estudios
     * @return retorna el plan de estudios que esta viendo el estudiante
     * @throws ExcepcionSolicitudes si el estudiante no existe
     */
    @Override
    public PlanEstudios extraerPlanEstudios(Estudiante e) throws ExcepcionSolicitudes{
        return extractorPlanEstudios.extraerPlanEstudios(e.getPlanEstudios(),e.getProgramaAcademico().getNombre());
    }
    
    /**
     * El sistema debe permitir seleccionar sólo 
     * aquellas asignaturas que esté actualmente 
     * viendo el usuario.
     * @param e
     * @return List of Asignatura.
     * @throws edu.eci.pdsw.samples.services.ExcepcionSolicitudes
     */
    @Transactional
    @Override
    public List<Asignatura> verMateriasActuales(Estudiante e) throws ExcepcionSolicitudes{
        return e.getMateriaActual();
    }


    @Override
    public void solicitarCancelacion(Estudiante e, Asignatura a, String justificacion, PlanEstudios planEstudios) throws ExcepcionSolicitudes {
        //Solicitudes mater= new SolicitudesImpl(e,a,justificacion, 9); 
        //Calcular e l impacto, despues crear una solcitud ccancelacion
        
        //Falta que daniel CIN implemente el calculador de impacto para que funcione
        
        //int resSc=calculadorDeImpacto.calcularImpacto(a, planEstudios);
        SolicitudCancelacion new2=new SolicitudCancelacion(justificacion, a, null, false);
        //int rep=calcularImpacto(e, a);
        
        
    }

    @Transactional
    @Override
    public void ajustarMaxCreditosSemestre(int numcreditos) throws ExcepcionSolicitudes {
        try {
            d.update(numcreditos);
        } catch (PersistenceException ex) {
            System.err.println(ex);
            throw new ExcepcionSolicitudes("No fue posible agregar los creditos");
        }
    }

    @Override
    public void agregarMateria(String programa, int plan, Asignatura a) throws ExcepcionSolicitudes {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void eliminarMateria(String programa, int plan, Asignatura a) throws ExcepcionSolicitudes {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean validarPlanEstudios(PlanEstudios pe) throws ExcepcionSolicitudes {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Asignatura> getAsignaturasPlanEstudios() {
        return asignaturasPlanEstudios;
    }

    public List<Asignatura> getVistasActualmente() {
        return vistasActualmente;
    }
    
    public List<SolicitudCancelacion> getSolicitudCancelacion() throws ExcepcionSolicitudes{
        List <SolicitudCancelacion> re=new ArrayList<SolicitudCancelacion>();
        try {             
             re=con.Read();
        } catch (PersistenceException ex) {
            Logger.getLogger(ServiciosCancelacionesImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return re;
        
    }
    
    
    public void cargarDatosPrueba(){
        
        ProgramaAcademico p1= new ProgramaAcademico(1, "Ingenieria Sistemas", 10, 210, 150);
      
        Asignatura as1 = new Asignatura(101, "Logica", p1, "Departamento Matematica", 504,3);
        Asignatura as2 = new Asignatura(102, "Modelos", p1, "Departamento Matematica", 505,4);
        Asignatura as3 = new Asignatura(103, "Redes", p1, "Departamento Matematica", 510,3);
        
        asignaturasPlanEstudios.add(as1);
        asignaturasPlanEstudios.add(as2);
        asignaturasPlanEstudios.add(as3);
        
        vistasActualmente.add(as1); 
        
        PlanEstudios plan= new PlanEstudios(1, 3, p1, asignaturasPlanEstudios);
        
        Estudiante est= new Estudiante(2104481, "daniel", "cas", 6,78, 001, 313, 9, vistasActualmente);

    }
    private void cargarDatosEstaticosGrafo()
    {
        
        
        Asignatura a1 =new Asignatura(0, "materia1", new ProgramaAcademico(), "pajarito", 1, 4);
        Asignatura a4 =new Asignatura(1, "materia4", new ProgramaAcademico(), "pajarito", 1, 3);
        Asignatura a5 =new Asignatura(2, "materia5", new ProgramaAcademico(), "pajarito", 1, 3);
        Asignatura a3 =new Asignatura(3, "materia3", new ProgramaAcademico(), "pajarito", 1, 4,Arrays.asList(a5));
        Asignatura a2 =new Asignatura(4, "materia2", new ProgramaAcademico(), "pajarito", 1, 2,Arrays.asList(a3,a4));
        
        PlanEstudios pe=new PlanEstudios(1, 5, new ProgramaAcademico(),Arrays.asList(a1,a2,a3,a4,a5));
        
    }

    @Override
    public int calcularImpacto(Estudiante e, Asignatura asig) throws ExcepcionSolicitudes {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    }

class Tupla<A, B> {

    A a;
    B b;

    public Tupla(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    @Override
    public int hashCode() {
        return a.hashCode() + b.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tupla<?, ?>) {
            return ((Tupla<?, ?>) obj).getA().equals(this.getA())
                    && ((Tupla<?, ?>) obj).getB().equals(this.getB());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Touple:(" + a.toString() + "," + b.toString() + ")";
    }
}
