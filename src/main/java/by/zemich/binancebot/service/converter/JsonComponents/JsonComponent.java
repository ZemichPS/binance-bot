package by.zemich.binancebot.service.converter.JsonComponents;


import by.zemich.binancebot.core.dto.binance.AccountInformationResponseDto;
import by.zemich.binancebot.core.dto.binance.BalanceDto;
import by.zemich.binancebot.core.dto.binance.CommissionRateDto;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import org.springframework.boot.jackson.JsonObjectDeserializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@org.springframework.boot.jackson.JsonComponent
public class JsonComponent {


    public static class UserCreateDtoDeserializer extends JsonObjectDeserializer<AccountInformationResponseDto> {

        @Override
        protected AccountInformationResponseDto deserializeObject(JsonParser jsonParser, DeserializationContext context, ObjectCodec codec, JsonNode tree) throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();

            AccountInformationResponseDto response = new AccountInformationResponseDto();
            CommissionRateDto commissionRate = new CommissionRateDto();
            List<BalanceDto> balanceDtoList = new ArrayList<>();

            response.setMakerCommission(tree.get("makerCommission").decimalValue());
            response.setTakerCommission(tree.get("takerCommission").decimalValue());
            response.setBuyerCommission(tree.get("buyerCommission").decimalValue());
            response.setSellerCommission(tree.get("sellerCommission").decimalValue());

            commissionRate.setMaker(new BigDecimal(tree.get("commissionRates").get("maker").textValue()).setScale(5));
            commissionRate.setTaker(new BigDecimal(tree.get("commissionRates").get("taker").textValue()).setScale(5));
            commissionRate.setBuyer(new BigDecimal(tree.get("commissionRates").get("buyer").textValue()).setScale(5));
            commissionRate.setSeller(new BigDecimal(tree.get("commissionRates").get("seller").textValue()).setScale(5));
            response.setCommissionRates(commissionRate);

            response.setCanTrade(tree.get("canTrade").booleanValue());
            response.setCanWithdraw(tree.get("canWithdraw").booleanValue());
            response.setCanDeposit(tree.get("canDeposit").booleanValue());
            response.setBrokered(tree.get("brokered").booleanValue());
            response.setRequireSelfTradePrevention(tree.get("requireSelfTradePrevention").booleanValue());
            response.setPreventSor(tree.get("preventSor").booleanValue());
            response.setUpdateTime(new Timestamp(tree.get("updateTime").asLong()));
            response.setAccountType(tree.get("accountType").asText());
            response.setUid(tree.get("uid").asLong());

            //System.out.println(tree.get("balances").toString());

            BalanceDto[] balanceArray = objectMapper.readValue(tree.get("balances").toString(), BalanceDto[].class);

            for (BalanceDto dto : balanceArray) {
                BalanceDto balanceDto = new BalanceDto();
                balanceDto.setAsset(dto.getAsset());
                balanceDto.setFree(dto.getFree().setScale(5, RoundingMode.HALF_UP));
                balanceDto.setLocked(dto.getLocked().setScale(5, RoundingMode.HALF_UP));

                balanceDtoList.add(balanceDto);
            }


            response.setBalances(balanceDtoList);

            return response;
        }
    }
}
//
//    public static class UserRegistrationDtoDeserializer extends JsonObjectDeserializer<UserRegistrationDto> {
//        @Override
//        protected UserRegistrationDto deserializeObject(JsonParser jsonParser, DeserializationContext context, ObjectCodec codec, JsonNode tree) throws IOException {
//            UserRegistrationDto dto = new UserRegistrationDto();
//            dto.setMail(tree.get("mail").textValue());
//            dto.setFullName(tree.get("fullName").textValue());
//            dto.setPassword(tree.get("password").textValue());
//            return dto;
//        }
//    }
//
//    public static class loginDtoDeserializer extends JsonObjectDeserializer<LoginDto> {
//
//        @Override
//        protected LoginDto deserializeObject(JsonParser jsonParser, DeserializationContext context, ObjectCodec codec, JsonNode tree) throws IOException {
//            LoginDto loginDto = new LoginDto(
//                    tree.get("mail").textValue(),
//                    tree.get("password").textValue()
//            );
//
//            return loginDto;
//        }
//    }
//
//    public static class PageOfDtoSerializer extends JsonObjectSerializer<PageOfDto<UserDto>> {
//        @Override
//        protected void serializeObject(PageOfDto<UserDto> value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
//            jgen.writeNumberField("number", value.getNumber());
//            jgen.writeNumberField("size", value.getSize());
//            jgen.writeNumberField("total_pages", value.getTotalPages());
//            jgen.writeNumberField("total_elements", value.getTotalElements());
//            jgen.writeBooleanField("first", value.isFirst());
//            jgen.writeNumberField("number_of_elements", value.getNumberOfElements());
//            jgen.writeBooleanField("last", value.isLast());
//            jgen.writeArrayFieldStart("content");
//
//            value.getContent().stream().forEach(userDto -> {
//                        try {
//                            jgen.writeStartObject();
//                            jgen.writeStringField("uuid", userDto.getUuid().toString());
//                            jgen.writeNumberField("dt_create", Timestamp.valueOf(userDto.getCreatedAt()).getTime());
//                            jgen.writeNumberField("dt_update", Timestamp.valueOf(userDto.getCreatedAt()).getTime());
//                            jgen.writeStringField("mail", userDto.getMail());
//                            jgen.writeStringField("fullName", userDto.getFullName());
//                            jgen.writeStringField("role", userDto.getRole().name());
//                            jgen.writeStringField("status", userDto.getStatus().name());
//                            jgen.writeEndObject();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//            );
//            jgen.writeEndArray();
//        }
//    }


