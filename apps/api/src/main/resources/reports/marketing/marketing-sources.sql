select source.code as source_code,
       source.name as source_name,
       source.sort_order as sort_order,
       count(partner.id) as business_partner_count,
       coalesce(sum(case when kind.code = 'LEAD' then 1 else 0 end), 0) as lead_count,
       coalesce(sum(case when kind.code = 'CUSTOMER' then 1 else 0 end), 0) as customer_count,
       coalesce(sum(case when kind.code = 'SUPPLIER' then 1 else 0 end), 0) as supplier_count,
       source.created_at as source_created_at,
       source.updated_at as source_updated_at
from lead_source source
left join business_partner partner on partner.lead_source_id = source.id and partner.archived_at is null
left join business_partner_kind kind on kind.id = partner.kind_id
where source.archived_at is null
  and (
       :query = ''
       or lower(source.code) like lower(concat('%', :query, '%'))
       or lower(source.name) like lower(concat('%', :query, '%'))
  )
group by source.id, source.code, source.name, source.sort_order, source.created_at, source.updated_at
order by source.sort_order asc, source.name asc, source.id asc
limit :limit
