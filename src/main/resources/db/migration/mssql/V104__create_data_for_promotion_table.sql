insert into promotion(
    [created_date] ,
    [created_by] ,
    [name] ,
    [apply_to],
    [account_id] ,
    [discount] ,
    [start_date] ,
    [end_date]
)
values (
   GETUTCDATE(),
   'system',
   'yearly',
   'Yearly',
   'admin',
   40,
   GETUTCDATE(),
   GETUTCDATE()
);