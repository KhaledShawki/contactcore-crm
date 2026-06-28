-- Copyright (c) Khaled Shawki. All rights reserved.

select partner.id as partner_id,
       partner.version as partner_version,
       partner.created_at as partner_created_at,
       partner.updated_at as partner_updated_at,
       kind.code as kind_code,
       status.code as status_code,
       status.name as status_name,
       source.code as source_code,
       partner.code as partner_code,
       partner.name as partner_name,
       email.value as primary_email,
       phone.value as primary_phone,
       website.value as website,
       primary_address.line1 as address_line1,
       primary_address.line2 as address_line2,
       primary_address.city as city,
       primary_address.postal_code as postal_code,
       primary_address.country_code as country_code,
       partner.notes as notes
from business_partner partner
join business_partner_kind kind on kind.id = partner.kind_id
join business_partner_status status on status.id = partner.status_id
left join lead_source source on source.id = partner.lead_source_id
left join lateral (
    select method.value
    from business_partner_contact_method method
    join contact_method_type type on type.id = method.contact_method_type_id
    where method.business_partner_id = partner.id
      and method.archived_at is null
      and method.primary_contact = true
      and type.code = 'EMAIL'
    order by method.id asc
    limit 1
) email on true
left join lateral (
    select method.value
    from business_partner_contact_method method
    join contact_method_type type on type.id = method.contact_method_type_id
    where method.business_partner_id = partner.id
      and method.archived_at is null
      and method.primary_contact = true
      and type.code = 'PHONE'
    order by method.id asc
    limit 1
) phone on true
left join lateral (
    select method.value
    from business_partner_contact_method method
    join contact_method_type type on type.id = method.contact_method_type_id
    where method.business_partner_id = partner.id
      and method.archived_at is null
      and method.primary_contact = true
      and type.code = 'WEBSITE'
    order by method.id asc
    limit 1
) website on true
left join lateral (
    select addr.line1,
           addr.line2,
           addr.city,
           addr.postal_code,
           addr.country_code
    from business_partner_address partner_address
    join address addr on addr.id = partner_address.address_id and addr.archived_at is null
    where partner_address.business_partner_id = partner.id
      and partner_address.archived_at is null
      and partner_address.primary_address = true
    order by partner_address.id asc
    limit 1
) primary_address on true
where partner.archived_at is null
  and kind.code = :kindCode
  /*{{SEARCH_PREDICATE}}*/
order by /*{{ORDER_BY}}*/
limit :limit offset :offset
