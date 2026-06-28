select kind.code as partner_type,
       partner.code as partner_code,
       partner.name as partner_name,
       status.name as status_name,
       coalesce(source.name, '') as marketing_source_name,
       coalesce(email.value, '') as primary_email,
       coalesce(phone.value, '') as primary_phone,
       coalesce(website.value, '') as website,
       coalesce(primary_person.first_name || ' ' || primary_person.last_name, '') as primary_contact_person,
       (
         select count(*) from business_partner_contact_person person_count
         where person_count.business_partner_id = partner.id and person_count.archived_at is null
       ) as contact_person_count,
       (
         select count(*) from business_partner_document document_count
         where document_count.business_partner_id = partner.id and document_count.archived_at is null
       ) as document_count,
       coalesce(addr.city, '') as city,
       coalesce(addr.country_code, '') as country_code,
       partner.created_at as partner_created_at,
       partner.updated_at as partner_updated_at,
       left(coalesce(partner.notes, ''), 2000) as notes
from business_partner partner
join business_partner_kind kind on kind.id = partner.kind_id
join business_partner_status status on status.id = partner.status_id
left join lead_source source on source.id = partner.lead_source_id
left join contact_method_type email_type on email_type.code = 'EMAIL'
left join contact_method_type phone_type on phone_type.code = 'PHONE'
left join contact_method_type website_type on website_type.code = 'WEBSITE'
left join business_partner_contact_method email on email.business_partner_id = partner.id
     and email.archived_at is null
     and email.primary_contact = true
     and email.contact_method_type_id = email_type.id
left join business_partner_contact_method phone on phone.business_partner_id = partner.id
     and phone.archived_at is null
     and phone.primary_contact = true
     and phone.contact_method_type_id = phone_type.id
left join business_partner_contact_method website on website.business_partner_id = partner.id
     and website.archived_at is null
     and website.primary_contact = true
     and website.contact_method_type_id = website_type.id
left join business_partner_contact_person primary_person on primary_person.business_partner_id = partner.id
     and primary_person.archived_at is null
     and primary_person.primary_contact = true
left join business_partner_address partner_address on partner_address.business_partner_id = partner.id
     and partner_address.archived_at is null
     and partner_address.primary_address = true
left join address addr on addr.id = partner_address.address_id and addr.archived_at is null
where partner.archived_at is null
  and kind.code = :kindCode
  and (
       :query = ''
       or lower(partner.code) like lower(concat('%', :query, '%'))
       or lower(partner.name) like lower(concat('%', :query, '%'))
       or lower(coalesce(email.value, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(phone.value, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(website.value, '')) like lower(concat('%', :query, '%'))
       or exists (
           select 1 from business_partner_contact_person person
           where person.business_partner_id = partner.id
             and person.archived_at is null
             and (
                  lower(concat(person.first_name, ' ', person.last_name)) like lower(concat('%', :query, '%'))
                  or lower(coalesce(person.email, '')) like lower(concat('%', :query, '%'))
                  or lower(coalesce(person.phone, '')) like lower(concat('%', :query, '%'))
                  or lower(coalesce(person.mobile, '')) like lower(concat('%', :query, '%'))
             )
       )
  )
order by /*{{ORDER_BY}}*/
limit :limit
