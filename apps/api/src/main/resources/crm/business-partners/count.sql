-- Copyright (c) Khaled Shawki. All rights reserved.

select count(partner.id)
from business_partner partner
join business_partner_kind kind on kind.id = partner.kind_id
where partner.archived_at is null
  and kind.code = :kindCode
  /*{{SEARCH_PREDICATE}}*/
