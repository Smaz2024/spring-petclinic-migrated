/*
 * Copyright 2002-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.web;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Vets;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for Vet-related actions.
 *
 * <p>
 * Handles displaying the list of veterinarians and providing JSON/XML
 * resource endpoints for the vet list.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Controller
public class VetController {

  private final ClinicService clinicService;

  /**
   * Constructor to inject the ClinicService.
  *
  * @param clinicService The clinic service.
  */
  @Autowired
  public VetController(ClinicService clinicService) {
    this.clinicService = clinicService;
  }

  /**
   * Shows the list of veterinarians in HTML format.
   *
   * @param model The model to be populated with the list of vets.
   * @return The view name for the vet list.
   */
  @GetMapping("/vets.html")
  public String showVetList(Map<String, Object> model) {
    // Here we are returning an object of type 'Vets' rather than a collection of
    // Vet
    // objects so it is simpler for Object-Xml mapping
    Vets vets = new Vets();
    vets.getVetList().addAll(this.clinicService.findVets());
    model.put("vets", vets);
    return "vets/vetList";
  }

  /**
   * Shows the list of veterinarians in JSON or XML format.
   *
   * @return The Vets object, which will be marshalled to JSON or XML.
   */
  @GetMapping(value = "/vets.json", produces = "application/json")
  @ResponseBody
  public Vets showResourcesVetListJson() {
    Vets vets = new Vets();
    vets.getVetList().addAll(this.clinicService.findVets());
    return vets;
  }

  @GetMapping(value = "/vets.xml", produces = "application/xml")
  @ResponseBody
  public Vets showResourcesVetListXml() {
    Vets vets = new Vets();
    vets.getVetList().addAll(this.clinicService.findVets());
    return vets;
  }

  /**
   * Generates a PDF of the veterinarians list.
   */
  @GetMapping(value = "/vets.pdf", produces = "application/pdf")
  @ResponseBody
  public void showVetListPdf(jakarta.servlet.http.HttpServletResponse response) {
    Vets vets = new Vets();
    vets.getVetList().addAll(this.clinicService.findVets());
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=vets.pdf");
    try {
      Document document = new Document();
      PdfWriter.getInstance(document, response.getOutputStream());
      document.open();
      document.add(new Paragraph("Veterinarians List"));
      com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(2);
      table.addCell("Name");
      table.addCell("Specialties");
      for (org.springframework.samples.petclinic.model.Vet vet : vets.getVetList()) {
        table.addCell(vet.getFirstName() + " " + vet.getLastName());
        if (vet.getSpecialties().isEmpty()) {
          table.addCell("none");
        } else {
          StringBuilder sb = new StringBuilder();
          for (org.springframework.samples.petclinic.model.Specialty s : vet.getSpecialties()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(s.getName());
          }
          table.addCell(sb.toString());
        }
      }
      document.add(table);
      document.close();
    } catch (Exception e) {
      throw new RuntimeException("Error generating PDF", e);
    }
  }
}
