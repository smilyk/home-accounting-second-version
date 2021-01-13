package smilyk.homeacc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import smilyk.homeacc.dto.BillDto;
import smilyk.homeacc.dto.OperationStatuDto;
import smilyk.homeacc.dto.UserDto;
import smilyk.homeacc.enums.Currency;
import smilyk.homeacc.enums.RequestOperationName;
import smilyk.homeacc.enums.RequestOperationStatus;
import smilyk.homeacc.service.bill.BillService;
import smilyk.homeacc.service.validation.ValidatorService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("v1/bill")
public class BillController {

    @Autowired
    BillService billService;

    @Autowired
    ValidatorService validatorService;

    /**
     * method create bill
     *
     * @param billDto
     * @return billDto
     */

    @PostMapping
    public BillDto createBill(@Validated @RequestBody BillDto billDto) {
        validatorService.checkUniqueBill(billDto.getBillName());
        validatorService.checkMainBill(billDto.getMainBill());
        if (billDto.getSumUkr() == null) {
            billDto.setSumUkr(0.0);
        }
        if (billDto.getSumIsr() == null) {
            billDto.setSumIsr(0.0);
        }
        if (billDto.getSumUsa() == null) {
            billDto.setSumUsa(0.0);
        }
        if (billDto.getDescription() == null) {
            billDto.setDescription("");
        }
        if (billDto.getCurrencyName() == null) {
            billDto.setCurrencyName(Currency.ISR);
        }
        if (billDto.getMainBill() == null) {
            billDto.setMainBill(false);
        }
        return billService.createBill(billDto);
    }

    /**
     * getting bill by bills name
     *
     * @param billName
     * @return BillDto
     */
    @GetMapping("/{billName}/{/userUuid}")
    public BillDto getBillByBillName(@RequestParam String billName, String userUuid) {
        return billService.getBillByBillName(billName, userUuid);
    }

    @GetMapping("/allBills/{userUuid}")
    public List<BillDto> getAllBillsByUserUuid(@RequestParam String userUuid) {
        return billService.getAllBillsByUser(userUuid);
    }

    /**
     * @param billName
     * @return BillDto
     */
    @PutMapping("/{billName}")
    public BillDto changeMailBill(@RequestParam String billName) {
        validatorService.ckeckBill(billName);
        return billService.changeMailBill(billName);
    }

    /**
     *
     * @param billName
     * @param userUuid
     * @return SUCCESS or ERROR
     */
    @DeleteMapping("/{billName}/{userUuid}")
    public OperationStatuDto deleteBill(@PathVariable String billName, String userUuid) {
        OperationStatuDto returnValue = new OperationStatuDto();
        returnValue.setOperationName(RequestOperationName.DELETE.name());
        billService.deleteUser(billName, userUuid);
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }
}