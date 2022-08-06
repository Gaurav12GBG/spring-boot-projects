package com.employee.employeesystem;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EmpController {

	@Autowired
	private EmpService service;

	@GetMapping("/")
	public String home(Model m) {
		List<EmpEntity> emp = service.getAllEmp();
		m.addAttribute("emp", emp);
		return "index";
	}

	@GetMapping("/createEmp")
	public String createEmp() {
		return "createEmp";
	}

	@PostMapping("/register")
	public String empRegister(@ModelAttribute EmpEntity e, HttpSession session) {
//		System.out.println(e);
		service.createEmp(e);
		session.setAttribute("msg", "Employee Added Successfully!!!");
		return "redirect:/";

	}

	@GetMapping("/editEmp/{id}")
	public String editEmp(@PathVariable int id, Model m, HttpSession session) {
		EmpEntity emp = service.getEmpById(id);
		m.addAttribute("emp", emp);
		return "editEmp";
	}

	@PostMapping("/updateEmp")
	public String updateEmpDetail(@ModelAttribute EmpEntity e, HttpSession session) {
		service.createEmp(e);
		session.setAttribute("msg", "Employee Details Successfully Updated!!!");
		return "redirect:/";
	}
	
	@GetMapping("/deleteEmp/{id}")
	public String deleteEmp(@PathVariable int id, HttpSession session) {
		service.deleteEmp(id);
		session.setAttribute("msg", "Employee Deleted Successfully!!!");
		return "redirect:/";
	}

}
