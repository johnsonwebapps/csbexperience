const PRODUCTS_MAP = {
  'business-checking': 'Business Checking',
  'business-interest-checking': 'Business Interest Checking',
  'business-savings': 'Business Savings',
  'business-money-market': 'Business Money Market',
  'business-cd': 'Business Certificate of Deposit',
}

const PURPOSE_MAP = {
  'new-business': 'New Business',
  'existing-business': 'Existing Business',
  'sole-proprietor': 'Sole Proprietor',
}

const BUSINESS_TYPE_MAP = {
  'sole-prop': 'Sole Proprietorship',
  'partnership': 'General Partnership',
  'llp': 'Limited Liability Partnership (LLP)',
  'llc-single': 'LLC – Single Member',
  'llc-multi': 'LLC – Multi Member',
  'corp-s': 'Corporation (S-Corp)',
  'corp-c': 'Corporation (C-Corp)',
  'nonprofit': 'Non-Profit Organization',
  'trust': 'Trust',
  'gov': 'Government Entity',
  'other': 'Other',
}

function ReviewSection({ title, step, onEdit, children }) {
  return (
    <div className="card">
      <div className="flex items-center justify-between border-b pb-3 mb-4">
        <h3 className="font-bold text-gray-800 text-lg">{title}</h3>
        <button
          className="text-csb-navy text-sm font-semibold hover:underline flex items-center gap-1"
          onClick={() => onEdit(step)}
        >
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
          </svg>
          Edit
        </button>
      </div>
      {children}
    </div>
  )
}

function DataRow({ label, value }) {
  if (!value) return null
  return (
    <div className="flex justify-between py-1.5 text-sm border-b border-gray-50 last:border-0">
      <span className="text-gray-500 font-medium">{label}</span>
      <span className="text-gray-900 font-semibold text-right max-w-xs">{value}</span>
    </div>
  )
}

function maskSSN(ssn) {
  if (!ssn) return ''
  return '•••-••-' + ssn.slice(-4)
}

export default function Review({ formData, updateFormData, onSubmit, onBack, onEdit }) {
  const allAgree = formData.agreeTerms && formData.agreeEsign && formData.agreePrivacy

  return (
    <div className="space-y-6">
      <div className="card">
        <h2 className="section-header">Review Your Application</h2>
        <p className="text-gray-500 text-sm">
          Please review all information carefully. Click "Edit" on any section to make changes, 
          or click "Submit Application" when everything looks correct.
        </p>
      </div>

      {/* Account selection */}
      <ReviewSection title="Account Selection" step={2} onEdit={onEdit}>
        <DataRow label="Purpose" value={PURPOSE_MAP[formData.accountPurpose]} />
        <DataRow label="Account Type" value={PRODUCTS_MAP[formData.selectedProduct]} />
      </ReviewSection>

      {/* Business info */}
      <ReviewSection title="Business Information" step={3} onEdit={onEdit}>
        <DataRow label="Legal Business Name" value={formData.businessName} />
        {formData.dba && <DataRow label="DBA" value={formData.dba} />}
        <DataRow label="Entity Type" value={BUSINESS_TYPE_MAP[formData.businessType]} />
        <DataRow label="EIN" value={formData.ein} />
        <DataRow label="State of Formation" value={formData.stateOfFormation} />
        <DataRow label="Date Established" value={formData.dateEstablished} />
        <DataRow label="Business Phone" value={formData.businessPhone} />
        <DataRow label="Business Address" value={`${formData.businessAddress}, ${formData.businessCity}, ${formData.businessState} ${formData.businessZip}`} />
        {formData.naics && <DataRow label="NAICS Code" value={formData.naics} />}
        <DataRow label="Business Description" value={formData.businessDescription} />
      </ReviewSection>

      {/* Applicant info */}
      <ReviewSection title="Primary Applicant" step={4} onEdit={onEdit}>
        <DataRow label="Full Name" value={`${formData.firstName} ${formData.lastName}`} />
        <DataRow label="Title" value={formData.title} />
        <DataRow label="Ownership" value={formData.ownershipPct ? `${formData.ownershipPct}%` : ''} />
        <DataRow label="Email" value={formData.email} />
        <DataRow label="Phone" value={formData.phone} />
        <DataRow label="Date of Birth" value={formData.dob} />
        <DataRow label="SSN" value={maskSSN(formData.ssn)} />
        <DataRow label="Home Address" value={`${formData.address}, ${formData.city}, ${formData.state} ${formData.zip}`} />
        <DataRow label="ID Type" value={formData.idType} />
        <DataRow label="ID Number" value={formData.idNumber ? '•••••' + formData.idNumber.slice(-4) : ''} />
        <DataRow label="ID Expiry" value={formData.idExpiry} />
      </ReviewSection>

      {/* Beneficial owners */}
      <ReviewSection title="Beneficial Ownership" step={5} onEdit={onEdit}>
        {formData.beneficialOwners.length === 0 ? (
          <p className="text-sm text-gray-500">No additional beneficial owners added.</p>
        ) : (
          formData.beneficialOwners.map((o, i) => (
            <div key={i} className="mb-3 pb-3 border-b border-gray-100 last:border-0">
              <p className="font-semibold text-csb-navy text-sm mb-2">Owner #{i + 1}</p>
              <DataRow label="Name" value={`${o.firstName} ${o.lastName}`} />
              <DataRow label="Title" value={o.title} />
              <DataRow label="Ownership" value={o.ownershipPct ? `${o.ownershipPct}%` : ''} />
              <DataRow label="SSN" value={maskSSN(o.ssn)} />
            </div>
          ))
        )}
        <DataRow label="Certification" value={formData.certifyBeneficialOwners ? '✓ Certified' : '⚠ Not certified'} />
      </ReviewSection>

      {/* Agreements */}
      <div className="card space-y-4">
        <h3 className="font-bold text-gray-800 text-lg border-b pb-3">Agreements & Disclosures</h3>
        <p className="text-sm text-gray-500">
          Please read and agree to the following to complete your application.
        </p>

        {[
          {
            key: 'agreeTerms',
            label: 'Account Terms & Conditions',
            text: 'I have read and agree to the Cambridge Savings Bank Business Account Terms and Conditions, including the Business Account Agreement and Fee Schedule.',
          },
          {
            key: 'agreeEsign',
            label: 'Electronic Communications (E-Sign)',
            text: 'I consent to receive account disclosures, statements, and communications electronically. I confirm I have the ability to access electronic documents.',
          },
          {
            key: 'agreePrivacy',
            label: 'Privacy Notice',
            text: 'I acknowledge receipt of the Cambridge Savings Bank Privacy Notice explaining how we collect, share, and protect your personal information.',
          },
        ].map(({ key, label, text }) => (
          <label key={key} className={`flex items-start gap-3 cursor-pointer p-3 rounded-lg transition-colors ${
            formData[key] ? 'bg-green-50' : 'hover:bg-gray-50'
          }`}>
            <input
              type="checkbox"
              className="mt-0.5 w-5 h-5 flex-shrink-0 accent-csb-navy"
              checked={formData[key]}
              onChange={e => updateFormData({ [key]: e.target.checked })}
            />
            <div>
              <div className="font-semibold text-sm text-gray-800">{label}</div>
              <div className="text-sm text-gray-600 mt-0.5">{text}</div>
            </div>
          </label>
        ))}

        {!allAgree && (
          <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-3 text-sm text-yellow-800">
            ⚠ Please agree to all disclosures above to submit your application.
          </div>
        )}
      </div>

      {/* FDIC / Legal */}
      <div className="text-xs text-gray-400 leading-relaxed text-center">
        Cambridge Savings Bank · Member FDIC · Equal Housing Lender<br />
        1374 Massachusetts Ave, Cambridge, MA 02138 · 1-888-418-5626<br />
        Your deposits are federally insured to at least $250,000 by the FDIC.
      </div>

      <div className="flex justify-between gap-3">
        <button className="btn-secondary" onClick={onBack}>← Back</button>
        <button
          className="btn-primary px-8"
          onClick={onSubmit}
          disabled={!allAgree}
          style={{ opacity: allAgree ? 1 : 0.5, cursor: allAgree ? 'pointer' : 'not-allowed' }}
        >
          Submit Application ✓
        </button>
      </div>
    </div>
  )
}
