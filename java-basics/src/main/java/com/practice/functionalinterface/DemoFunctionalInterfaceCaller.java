package com.practice.functionalinterface;

public class DemoFunctionalInterfaceCaller {

    public static void main(String[] args) {

        DemoFunctionalInterfaceService demoFunctionalInterfaceService = new DemoFunctionalInterfaceService();
        demoFunctionalInterfaceService.execute(() -> System.out.println("Running something"));

    }
}
