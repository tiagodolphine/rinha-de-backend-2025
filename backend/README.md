


(payment controller) -> queue 
queue -> worker -> () -> (retry volta pra fila) processorService : default      -> save(amount, req, processorType, date)
                                                    fallback
                                                        |                        
                                            processorChooser (health check or receive an event responsetime, error 500)



queue
- correlation (pk)
- amount
- state



summary (transaction table)

indx(type, timestamp)
type:
ammount:
timestamp:

    "default" : {
        "totalRequests": 43236,
        "totalAmount": 415542345.98
    },
    "fallback" : {
        "totalRequests": 423545,
        "totalAmount": 329347.34
    }

