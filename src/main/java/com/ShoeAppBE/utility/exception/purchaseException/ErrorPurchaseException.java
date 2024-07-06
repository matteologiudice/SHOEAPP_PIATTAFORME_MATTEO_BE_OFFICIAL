package com.ShoeAppBE.utility.exception.purchaseException;

import com.ShoeAppBE.utility.other.ResponseOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ErrorPurchaseException extends Exception {

    List<ResponseOrder> product;

    public ErrorPurchaseException(List<ResponseOrder> product) { this.product = product; }
}
