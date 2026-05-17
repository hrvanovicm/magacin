PRAGMA writable_schema = 1;
UPDATE sqlite_master
SET sql = REPLACE(sql,
    'CHECK (type IN (''RECEIPT'', ''SHIPMENT''))',
    'CHECK (type IN (''RECEIPT'', ''SHIPMENT'', ''WORK_ORDER''))')
WHERE type = 'table' AND name = 'reports';
PRAGMA writable_schema = 0;
