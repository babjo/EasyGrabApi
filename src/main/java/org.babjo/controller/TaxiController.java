package org.babjo.controller;

import org.babjo.domain.TaxiData;
import org.babjo.dto.GetTaxiDataNearMeRequestDTO;
import org.babjo.dto.GetTaxiDataNearMeResponseDTO;
import org.babjo.dto.SuccessDTO;
import org.babjo.service.TaxiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * Created by LCH on 2016. 10. 23..
 */
@Controller
@RequestMapping("taxi")
public class TaxiController {

    @Autowired
    TaxiService taxiService;

    @RequestMapping(value = "/getTaxiDataNearMe", method = RequestMethod.POST)
    @ResponseBody
    public SuccessDTO getTaxiDataNearMe(@RequestBody @Valid GetTaxiDataNearMeRequestDTO arg){
        GetTaxiDataNearMeResponseDTO g = taxiService.getTextDataNearMe(arg);
        return new SuccessDTO(g);
    }
}
