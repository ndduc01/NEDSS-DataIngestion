CREATE TABLE [NBS_DataIngest].[dbo].[elr_dlt] (
    error_message_id UNIQUEIDENTIFIER PRIMARY KEY,
    error_message_source nvarchar(255) not null,
    error_stack_trace nvarchar(max) not null,
    error_stack_trace_short nvarchar(max) not null,
    dlt_status nvarchar(10) not null,
    dlt_occurrence int,
    message ntext not null,
    created_by nvarchar(255) not null,
    updated_by nvarchar(255) not null,
    created_on DATETIME not null default getdate(),
    updated_on DATETIME null
)