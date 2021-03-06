package smilyk.homeacc.service.inputCard;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smilyk.homeacc.constants.BillConstants;
import smilyk.homeacc.constants.InputCardConstant;
import smilyk.homeacc.constants.OutputCardConstant;
import smilyk.homeacc.dto.InputCardDto;
import smilyk.homeacc.enums.Currency;
import smilyk.homeacc.exceptions.HomeaccException;
import smilyk.homeacc.model.Bill;
import smilyk.homeacc.model.InputCard;
import smilyk.homeacc.repo.BillRepository;
import smilyk.homeacc.repo.InputCardRepository;
import smilyk.homeacc.service.user.UserServiceImpl;
import smilyk.homeacc.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InputCardServiceImpl implements InputCardService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    ModelMapper modelMapper = new ModelMapper();
    @Autowired
    Utils utils;
    @Autowired
    BillRepository billRepository;
    @Autowired
    InputCardRepository inputCardRepository;

    @Override
    public InputCardDto createInputCard(InputCardDto inputCardDto) {
        Bill bill = billRepository.findByBillNameAndUserUuidAndDeleted(inputCardDto.getBillName(),
            inputCardDto.getUserUuid(), false).get();
        billRepository.delete(bill);
        bill = changeSum(inputCardDto, bill);
        billRepository.save(bill);
        LOGGER.info(BillConstants.BILL_SUM + BillConstants.FOR + BillConstants.BILL_WITH_NAME
            +bill.getBillName() + BillConstants.CHANGED);
        InputCard inputCard = getInputCard(inputCardDto);
        inputCardRepository.save(inputCard);
        LOGGER.info(InputCardConstant.INPUT_CARD + inputCard.getUserUuid() +
            InputCardConstant.CREATED);
        return modelMapper.map(inputCard, InputCardDto.class);
//        TODO test
    }

    @Override
    public List<InputCardDto> getAllInputCardsByUserUuid(String userUuid){
        Optional<List<InputCard>> optionalInputCards = inputCardRepository.findAllByUserUuid(userUuid);
        return optionalInputCards.map(categories -> categories.stream().map(this::inputCardToInputCardDto)
            .collect(Collectors.toList())).orElseGet(ArrayList::new);

    }

    @Override
    public InputCardDto deleteInputCard(String inputCardUuid) {
        Optional<InputCard> optionalInputCard = inputCardRepository.findByInputCardUuid(inputCardUuid);
        inputCardRepository.delete(optionalInputCard.get());
        LOGGER.info(InputCardConstant.INPUT_CARD_WITH_UUID + inputCardUuid + InputCardConstant.DELETED);
        return modelMapper.map(optionalInputCard.get(), InputCardDto.class);
    }

    @Override
    public InputCardDto getInputCardByUuid(String userUuid, String inputCardUuid) {
        //        TODO test
        Optional<InputCard> optionalInputCard = inputCardRepository.findByUserUuidAndInputCardUuid(
            userUuid, inputCardUuid);
        if(!optionalInputCard.isPresent()){
            LOGGER.error(OutputCardConstant.INPUT_CARD_WITH_UUID + inputCardUuid + " or "
                + OutputCardConstant.INPUT_CARD + userUuid + OutputCardConstant.NOT_FOUND);
            throw new HomeaccException(OutputCardConstant.OUTPUT_CARD_WITH_UUID + inputCardUuid + " or "
                + OutputCardConstant.OUTPUT_CARD + userUuid + OutputCardConstant.NOT_FOUND);
        }
        return modelMapper.map(optionalInputCard.get(), InputCardDto.class);
    }

    @Override
    public List<InputCard> getAllInputCardsByUserUuidAndDate(String userUuid, Date chosenDate) {
        String date = chosenDate.toInstant().toString();
        return inputCardRepository.getListInputsCardByUserAndDate(userUuid, date);
    }

    // TODO test
    private InputCardDto inputCardToInputCardDto(InputCard inputCard) {
        return modelMapper.map(inputCard, InputCardDto.class);
    }

    private InputCard getInputCard(InputCardDto inputCardDto) {
        return InputCard.builder()
            .userUuid(inputCardDto.getUserUuid())
            .inputCardUuid(utils.generateUserUuid().toString())
            .deleted(false)
            .subcategoryUuid(inputCardDto.getSubcategoryUuid())
            .subcategoryName(inputCardDto.getSubcategoryName())
            .billUuid(inputCardDto.getBillUuid())
            .billName(inputCardDto.getBillName())
            .categoryUuid(inputCardDto.getCategoryUuid())
            .categoryName(inputCardDto.getCategoryName())
            .count(inputCardDto.getCount())
            .currency(inputCardDto.getCurrency())
            .note(inputCardDto.getNote())
            .sum(inputCardDto.getSum())
            .unit(inputCardDto.getUnit())
            .createCardDate(inputCardDto.getCreateCardDate())
            .build();
    }

    private Bill changeSum(InputCardDto inputCardDto, Bill bill) {
        if (inputCardDto.getCurrency().equals(Currency.USA)) {
            bill.setSumUsa(bill.getSumUsa() + inputCardDto.getSum());
        }
        else if (inputCardDto.getCurrency().equals(Currency.ISR)) {
            bill.setSumIsr(bill.getSumIsr() + inputCardDto.getSum());
        }
        else if (inputCardDto.getCurrency().equals(Currency.UKR)) {
            bill.setSumUkr(bill.getSumUkr() + inputCardDto.getSum());
        }
        return bill;
    }
}
